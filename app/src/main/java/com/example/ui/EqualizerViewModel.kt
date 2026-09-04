package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MEqualizerApp
import com.example.audio.AudioEngine
import com.example.audio.VisualizerEngine
import com.example.data.model.AudioPreset
import com.example.data.model.DeviceProfile
import com.example.data.model.DeviceType
import com.example.data.repository.DeviceProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EqualizerViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as MEqualizerApp
    private val repository: DeviceProfileRepository = app.repository
    private val audioEngine: AudioEngine = app.audioEngine
    private val visualizerEngine = VisualizerEngine(application, viewModelScope)

    val audioState = audioEngine.audioState
    val spectrumData = visualizerEngine.spectrumData
    val peakData = visualizerEngine.peakData
    val waveformData = visualizerEngine.waveformData

    private val _currentProfile = MutableStateFlow(
        DeviceProfile(
            id = DeviceType.HEADPHONES.id,
            name = "Headphones",
            deviceType = DeviceType.HEADPHONES.name,
            isEnabled = true,
            eqBands = listOf(3, 2, 1, 0, -1, 1, 2, 3, 4, 3),
            bassBoost = 35,
            loudnessGain = 25,
            spatialSurround = 40,
            reverbPreset = 1,
            limiterEnabled = true,
            agcEnabled = true,
            presetName = "Studio Master",
            isCurrent = true
        )
    )
    val currentProfile: StateFlow<DeviceProfile> = _currentProfile.asStateFlow()

    private val _selectedDeviceType = MutableStateFlow(DeviceType.HEADPHONES)
    val selectedDeviceType: StateFlow<DeviceType> = _selectedDeviceType.asStateFlow()

    private var autoSaveJob: Job? = null

    init {
        // Observe repository current profile
        viewModelScope.launch {
            repository.currentProfile.collectLatest { profile ->
                profile?.let {
                    _currentProfile.value = it
                    _selectedDeviceType.value = DeviceType.fromId(it.id)
                    audioEngine.applyProfile(it)
                    audioEngine.setEnabled(it.isEnabled)
                    visualizerEngine.updateEqProfile(it.eqBands, it.isEnabled)
                }
            }
        }
    }

    /**
     * Master Power Switch (ON/OFF).
     */
    fun toggleMasterPower(enabled: Boolean) {
        val updated = _currentProfile.value.copy(isEnabled = enabled)
        _currentProfile.value = updated
        audioEngine.setEnabled(enabled)
        visualizerEngine.updateEqProfile(updated.eqBands, enabled)
        scheduleAutoSave(updated)
    }

    /**
     * Select a device profile (Headphones, Earbuds, Car Stereo, Speakers).
     */
    fun selectDevice(type: DeviceType) {
        _selectedDeviceType.value = type
        viewModelScope.launch {
            repository.selectProfile(type.id)
        }
    }

    /**
     * Instantaneous single EQ band update (sub-millisecond latency).
     */
    fun updateEqBand(index: Int, levelDb: Int) {
        val bands = _currentProfile.value.eqBands.toMutableList()
        if (index in bands.indices) {
            bands[index] = levelDb
            val updated = _currentProfile.value.copy(
                eqBands = bands,
                presetName = "Custom"
            )
            _currentProfile.value = updated
            // Instant hardware apply
            audioEngine.applyProfile(updated)
            visualizerEngine.updateEqProfile(bands, updated.isEnabled)
            scheduleAutoSave(updated)
        }
    }

    /**
     * Instantaneous Bass Boost update.
     */
    fun updateBassBoost(percent: Int) {
        val updated = _currentProfile.value.copy(bassBoost = percent)
        _currentProfile.value = updated
        audioEngine.applyProfile(updated)
        scheduleAutoSave(updated)
    }

    /**
     * Instantaneous Loudness Gain update.
     */
    fun updateLoudnessGain(percent: Int) {
        val updated = _currentProfile.value.copy(loudnessGain = percent)
        _currentProfile.value = updated
        audioEngine.applyProfile(updated)
        scheduleAutoSave(updated)
    }

    /**
     * Instantaneous 360° 3D Spatial Audio update.
     */
    fun updateSpatialSurround(percent: Int) {
        val updated = _currentProfile.value.copy(spatialSurround = percent)
        _currentProfile.value = updated
        audioEngine.applyProfile(updated)
        scheduleAutoSave(updated)
    }

    /**
     * Reverb Preset update.
     */
    fun updateReverbPreset(presetCode: Int) {
        val updated = _currentProfile.value.copy(reverbPreset = presetCode)
        _currentProfile.value = updated
        audioEngine.applyProfile(updated)
        scheduleAutoSave(updated)
    }

    /**
     * Anti-clipping Brickwall Limiter toggle.
     */
    fun toggleLimiter(enabled: Boolean) {
        val updated = _currentProfile.value.copy(limiterEnabled = enabled)
        _currentProfile.value = updated
        audioEngine.applyProfile(updated)
        scheduleAutoSave(updated)
    }

    /**
     * Automatic Gain Control toggle.
     */
    fun toggleAgc(enabled: Boolean) {
        val updated = _currentProfile.value.copy(agcEnabled = enabled)
        _currentProfile.value = updated
        audioEngine.applyProfile(updated)
        scheduleAutoSave(updated)
    }

    /**
     * Apply a studio preset.
     */
    fun applyPreset(preset: AudioPreset) {
        val updated = _currentProfile.value.copy(
            eqBands = preset.bands,
            bassBoost = preset.bassBoost,
            loudnessGain = preset.loudnessGain,
            spatialSurround = preset.spatialSurround,
            reverbPreset = preset.reverbPreset,
            presetName = preset.name
        )
        _currentProfile.value = updated
        audioEngine.applyProfile(updated)
        visualizerEngine.updateEqProfile(preset.bands, updated.isEnabled)
        scheduleAutoSave(updated)
    }

    /**
     * Resets current profile to completely flat.
     */
    fun resetToFlat() {
        applyPreset(AudioPreset.BUILT_IN_PRESETS.first())
    }

    fun attachVisualizerSession(sessionId: Int) {
        visualizerEngine.attachSession(sessionId)
    }

    /**
     * Debounces Room DB saves so UI slider dragging is 100% fluid with zero I/O blocking.
     */
    private fun scheduleAutoSave(profile: DeviceProfile) {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(350) // 350ms debounce for database persistence
            repository.saveProfile(profile)
        }
    }

    override fun onCleared() {
        super.onCleared()
        visualizerEngine.destroy()
    }
}
