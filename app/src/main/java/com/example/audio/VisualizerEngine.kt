package com.example.audio

import android.content.Context
import android.content.pm.PackageManager
import android.media.audiofx.Visualizer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * High-performance visualizer engine.
 * Safely hooks Android native Visualizer on the active audio session,
 * and maintains continuous 60fps spectrum animation physics (gravity, peak hold, dynamic reactivity).
 */
class VisualizerEngine(
    private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "VisualizerEngine"
        const val NUM_BANDS = 24
    }

    // Normalized frequency band magnitudes: 0.0f to 1.0f
    private val _spectrumData = MutableStateFlow(FloatArray(NUM_BANDS) { 0.1f })
    val spectrumData: StateFlow<FloatArray> = _spectrumData.asStateFlow()

    // Peak levels for peak hold caps
    private val _peakData = MutableStateFlow(FloatArray(NUM_BANDS) { 0.15f })
    val peakData: StateFlow<FloatArray> = _peakData.asStateFlow()

    // Waveform points for oscilloscope display
    private val _waveformData = MutableStateFlow(FloatArray(64) { 0f })
    val waveformData: StateFlow<FloatArray> = _waveformData.asStateFlow()

    private var nativeVisualizer: Visualizer? = null
    private var isRealVisualizerAttached = false
    private var animationJob: Job? = null
    private var currentEqBands: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var isPlaying = true

    init {
        startAnimationLoop()
    }

    fun updateEqProfile(bands: List<Int>, isEngineEnabled: Boolean) {
        currentEqBands = bands
        isPlaying = isEngineEnabled
    }

    fun attachSession(sessionId: Int) {
        releaseNativeVisualizer()
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Log.d(TAG, "Audio record permission not granted, using dynamic reactive synthesizer")
            return
        }

        try {
            val visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            visualizer: Visualizer?,
                            waveform: ByteArray?,
                            samplingRate: Int
                        ) {
                            waveform?.let { processWaveform(it) }
                        }

                        override fun onFftDataCapture(
                            visualizer: Visualizer?,
                            fft: ByteArray?,
                            samplingRate: Int
                        ) {
                            fft?.let { processFft(it) }
                        }
                    },
                    Visualizer.getMaxCaptureRate() / 2,
                    true,
                    true
                )
                enabled = true
            }
            nativeVisualizer = visualizer
            isRealVisualizerAttached = true
            Log.d(TAG, "Native visualizer hooked to session $sessionId")
        } catch (e: Exception) {
            Log.w(TAG, "Native visualizer attach notice (fallback active): ${e.message}")
            isRealVisualizerAttached = false
        }
    }

    private fun processWaveform(waveform: ByteArray) {
        val step = (waveform.size / 64).coerceAtLeast(1)
        val wavePoints = FloatArray(64)
        for (i in 0 until 64) {
            val byteVal = waveform.getOrElse(i * step) { 0 }
            wavePoints[i] = (byteVal.toInt() - 128) / 128f
        }
        _waveformData.value = wavePoints
    }

    private fun processFft(fft: ByteArray) {
        // Compute magnitudes from complex FFT pairs
        val bands = FloatArray(NUM_BANDS)
        val peaks = _peakData.value.copyOf()
        val n = fft.size / 2
        val bandWidth = (n / NUM_BANDS).coerceAtLeast(1)

        for (i in 0 until NUM_BANDS) {
            var sum = 0f
            val start = i * bandWidth
            val end = (start + bandWidth).coerceAtMost(n)
            for (j in start until end) {
                val real = fft[2 * j].toFloat()
                val imag = fft[2 * j + 1].toFloat()
                val mag = sqrt(real * real + imag * imag)
                sum += mag
            }
            val avg = if (end > start) sum / (end - start) else 0f
            // Apply logarithmic scaling
            val normalized = (avg / 120f).coerceIn(0.05f, 1.0f)
            bands[i] = normalized

            // Update peak hold with decay
            if (normalized > peaks[i]) {
                peaks[i] = normalized
            } else {
                peaks[i] = (peaks[i] - 0.02f).coerceAtLeast(0.05f)
            }
        }
        _spectrumData.value = bands
        _peakData.value = peaks
    }

    private fun startAnimationLoop() {
        animationJob?.cancel()
        animationJob = scope.launch(Dispatchers.Default) {
            var phase = 0f
            val currentBands = FloatArray(NUM_BANDS) { 0.1f }
            val currentPeaks = FloatArray(NUM_BANDS) { 0.15f }
            val currentWave = FloatArray(64) { 0f }

            while (isActive) {
                if (!isRealVisualizerAttached) {
                    phase += 0.12f
                    val power = if (isPlaying) 1.0f else 0.05f

                    for (i in 0 until NUM_BANDS) {
                        // Map band index to 10-band EQ influence
                        val eqIndex = ((i.toFloat() / NUM_BANDS) * 9f).toInt().coerceIn(0, 9)
                        val eqBoostDb = currentEqBands.getOrElse(eqIndex) { 0 }
                        val eqMultiplier = (1f + (eqBoostDb / 20f)).coerceIn(0.4f, 2.0f)

                        // Multi-frequency wave simulation with organic harmonics
                        val w1 = sin(phase * 1.8f + i * 0.45f)
                        val w2 = cos(phase * 2.7f - i * 0.3f)
                        val w3 = sin(phase * 0.9f + i * 0.8f)
                        val noise = Random.nextFloat() * 0.15f

                        val raw = (abs(w1 * 0.45f + w2 * 0.35f + w3 * 0.2f) + noise) * eqMultiplier * power
                        val target = raw.coerceIn(0.05f, 0.98f)

                        // Smooth interpolation (attack / decay physics)
                        if (target > currentBands[i]) {
                            currentBands[i] = currentBands[i] + (target - currentBands[i]) * 0.55f // fast attack
                        } else {
                            currentBands[i] = currentBands[i] - 0.035f // smooth gravity decay
                        }
                        currentBands[i] = currentBands[i].coerceIn(0.05f, 1.0f)

                        // Peak hold logic
                        if (currentBands[i] > currentPeaks[i]) {
                            currentPeaks[i] = currentBands[i]
                        } else {
                            currentPeaks[i] = (currentPeaks[i] - 0.015f).coerceAtLeast(0.05f)
                        }
                    }

                    // Generate smooth oscilloscope waveform
                    for (k in 0 until 64) {
                        val waveVal = sin(phase * 3f + k * 0.25f) * 0.6f * power +
                                sin(phase * 1.5f + k * 0.1f) * 0.3f * power
                        currentWave[k] = waveVal
                    }

                    _spectrumData.value = currentBands.copyOf()
                    _peakData.value = currentPeaks.copyOf()
                    _waveformData.value = currentWave.copyOf()
                }
                delay(16) // ~60 fps
            }
        }
    }

    fun releaseNativeVisualizer() {
        try {
            nativeVisualizer?.enabled = false
            nativeVisualizer?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Visualizer release notice: ${e.message}")
        } finally {
            nativeVisualizer = null
            isRealVisualizerAttached = false
        }
    }

    fun destroy() {
        animationJob?.cancel()
        releaseNativeVisualizer()
    }
}
