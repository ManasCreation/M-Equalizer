package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanHighlight
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ImmersiveBorderSubtle
import com.example.ui.theme.ImmersiveCardBgTranslucent
import com.example.ui.theme.TextMuted

@Composable
fun LiveAudioVisualizer(
    spectrumData: FloatArray,
    peakData: FloatArray,
    waveformData: FloatArray,
    isEngineActive: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_scan")
    val scanLineFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_anim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ImmersiveCardBgTranslucent)
            .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("live_audio_visualizer"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Spectrum and Oscilloscope Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val baselineY = height

                // Spectrum Bars (Immersive Cyan tones with glow)
                val numBands = spectrumData.size.coerceAtLeast(1)
                val barSpacing = 2.5.dp.toPx()
                val totalSpacing = barSpacing * (numBands + 1)
                val barWidth = ((width - totalSpacing) / numBands).coerceAtLeast(3f)

                for (i in 0 until numBands) {
                    val mag = spectrumData.getOrElse(i) { 0.05f }.coerceIn(0.06f, 1.0f)
                    val peak = peakData.getOrElse(i) { 0.05f }.coerceIn(0.06f, 1.0f)

                    val barHeight = mag * height * 0.95f
                    val barX = barSpacing + i * (barWidth + barSpacing)
                    val barTopY = baselineY - barHeight

                    // Subtle cyan gradation matching the design
                    val barAlpha = when {
                        mag > 0.75f -> 0.95f
                        mag > 0.50f -> 0.70f
                        mag > 0.25f -> 0.45f
                        else -> 0.25f
                    }
                    val barColor = if (mag > 0.8f) CyanHighlight else CyanPrimary

                    drawRoundRect(
                        color = barColor.copy(alpha = barAlpha),
                        topLeft = Offset(barX, barTopY),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // Peak indicator cap with soft glow
                    val peakTopY = (baselineY - (peak * height * 0.95f)).coerceAtLeast(4.dp.toPx())
                    drawRoundRect(
                        color = CyanGlow,
                        topLeft = Offset(barX, peakTopY - 2.dp.toPx()),
                        size = Size(barWidth, 2.dp.toPx()),
                        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
                    )
                }

                // Oscilloscope Waveform overlay
                if (waveformData.isNotEmpty() && isEngineActive) {
                    val wavePath = Path()
                    val waveMidY = height * 0.45f
                    val waveScaleY = height * 0.25f
                    val stepX = width / (waveformData.size - 1).coerceAtLeast(1)

                    waveformData.forEachIndexed { index, sample ->
                        val x = index * stepX
                        val y = waveMidY + (sample * waveScaleY)
                        if (index == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                    }

                    drawPath(
                        path = wavePath,
                        color = CyanGlow.copy(alpha = 0.45f),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }

                // Sci-fi scanline shimmer
                if (isEngineActive) {
                    val scanY = height * scanLineFraction
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, CyanGlow.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        start = Offset(0f, scanY),
                        end = Offset(width, scanY),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }
        }

        // Bottom Frequency & Real-Time Readout matching Design HTML: 20Hz • REAL-TIME SPECTRUM • 20kHz
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "20Hz",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted
            )
            Text(
                text = if (isEngineActive) "REAL-TIME SPECTRUM" else "DSP BYPASSED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                color = if (isEngineActive) CyanPrimary.copy(alpha = 0.9f) else TextMuted
            )
            Text(
                text = "20kHz",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted
            )
        }
    }
}
