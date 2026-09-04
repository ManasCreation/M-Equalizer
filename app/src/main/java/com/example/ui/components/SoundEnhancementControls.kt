package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.ui.theme.ImmersiveInnerBg
import com.example.ui.theme.ImmersiveSlate700
import com.example.ui.theme.ImmersiveSlate800
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SoundEnhancementControls(
    bassBoost: Int,
    onBassBoostChanged: (Int) -> Unit,
    loudnessGain: Int,
    onLoudnessGainChanged: (Int) -> Unit,
    spatialSurround: Int,
    onSpatialSurroundChanged: (Int) -> Unit,
    reverbPreset: Int,
    onReverbPresetChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sound_enhancement_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Grid of 3 Dial Cards matching Design HTML:
        // grid grid-cols-3 gap-4 with bg-slate-900/60 border border-white/5 rounded-3xl p-4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Bass Boost Dial
            EnhancementDialCard(
                title = "BASS BOOST",
                valueText = if (bassBoost == 0) "0" else "$bassBoost%",
                fraction = (bassBoost / 100f).coerceIn(0f, 1f),
                isActive = bassBoost > 0,
                onIncrement = {
                    val nextVal = if (bassBoost >= 100) 0 else (bassBoost + 25).coerceAtMost(100)
                    onBassBoostChanged(nextVal)
                },
                onDragDelta = { delta ->
                    val newVal = (bassBoost - delta * 0.5f).toInt().coerceIn(0, 100)
                    onBassBoostChanged(newVal)
                },
                modifier = Modifier.weight(1f),
                testTag = "dial_bass_boost"
            )

            // 2. 3D Spatial Dial
            EnhancementDialCard(
                title = "3D SPATIAL",
                valueText = if (spatialSurround == 0) "OFF" else if (spatialSurround == 100) "ON" else "$spatialSurround%",
                fraction = (spatialSurround / 100f).coerceIn(0f, 1f),
                isActive = spatialSurround > 0,
                onIncrement = {
                    val nextVal = if (spatialSurround >= 100) 0 else (spatialSurround + 25).coerceAtMost(100)
                    onSpatialSurroundChanged(nextVal)
                },
                onDragDelta = { delta ->
                    val newVal = (spatialSurround - delta * 0.5f).toInt().coerceIn(0, 100)
                    onSpatialSurroundChanged(newVal)
                },
                modifier = Modifier.weight(1f),
                testTag = "dial_spatial_surround"
            )

            // 3. Loudness Dial
            EnhancementDialCard(
                title = "LOUDNESS",
                valueText = if (loudnessGain == 0) "0" else "+$loudnessGain%",
                fraction = (loudnessGain / 100f).coerceIn(0f, 1f),
                isActive = loudnessGain > 0,
                onIncrement = {
                    val nextVal = if (loudnessGain >= 100) 0 else (loudnessGain + 25).coerceAtMost(100)
                    onLoudnessGainChanged(nextVal)
                },
                onDragDelta = { delta ->
                    val newVal = (loudnessGain - delta * 0.5f).toInt().coerceIn(0, 100)
                    onLoudnessGainChanged(newVal)
                },
                modifier = Modifier.weight(1f),
                testTag = "dial_loudness"
            )
        }

        // Reverb Environment Selector Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ImmersiveCardBgTranslucent)
                .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(24.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REVERB ENVIRONMENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                val reverbNames = listOf("Off", "Small Room", "Medium Room", "Large Room", "Medium Hall", "Large Hall", "Plate")
                Text(
                    text = reverbNames.getOrElse(reverbPreset) { "Off" }.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (reverbPreset > 0) CyanGlow else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val reverbOptions = listOf(
                Pair(0, "Off"),
                Pair(1, "Small Room"),
                Pair(2, "Medium Room"),
                Pair(3, "Large Room"),
                Pair(4, "Medium Hall"),
                Pair(5, "Large Hall"),
                Pair(6, "Plate Reverb")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reverbOptions.forEach { (presetCode, label) ->
                    val isSelected = reverbPreset == presetCode
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CyanPrimary else ImmersiveInnerBg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CyanGlow else ImmersiveBorderSubtle,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onReverbPresetChanged(presetCode) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("reverb_preset_$presetCode")
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Circular Arc Dial Card matching Design HTML:
 * - relative w-14 h-14 rounded-full border-4 border-slate-800
 * - border-4 border-cyan-500 arc overlay
 * - label: text-[9px] font-bold text-slate-400 uppercase
 */
@Composable
private fun EnhancementDialCard(
    title: String,
    valueText: String,
    fraction: Float,
    isActive: Boolean,
    onIncrement: () -> Unit,
    onDragDelta: (Float) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(150),
        label = "dial_anim"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(ImmersiveCardBgTranslucent)
            .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(24.dp))
            .clickable { onIncrement() }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDragDelta(dragAmount.y)
                }
            }
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Circular progress dial
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 4.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background track ring (border-4 border-slate-800)
                drawCircle(
                    color = ImmersiveSlate800,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Cyan Progress Arc (border-4 border-cyan-500)
                if (animatedFraction > 0.01f) {
                    val sweepAngle = animatedFraction * 360f
                    drawArc(
                        color = CyanPrimary,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // Value text inside ring (text-xs font-bold text-white)
            Text(
                text = valueText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isActive) Color.White else TextMuted
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title label: text-[9px] font-bold text-slate-400 uppercase
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = TextSecondary,
            maxLines = 1
        )
    }
}
