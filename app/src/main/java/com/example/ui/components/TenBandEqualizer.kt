package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioPreset
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPillBg
import com.example.ui.theme.CyanPillBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ImmersiveBorderSubtle
import com.example.ui.theme.ImmersiveCardBgTranslucent
import com.example.ui.theme.ImmersiveInnerBg
import com.example.ui.theme.ImmersiveSlate700
import com.example.ui.theme.ImmersiveSlate800
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TenBandEqualizer(
    bands: List<Int>, // 10 band levels (-15 to +15 dB)
    presetName: String = "Studio Master",
    onBandChanged: (index: Int, valueDb: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ImmersiveCardBgTranslucent)
            .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("ten_band_equalizer"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header matching Design HTML: 10-Band EQ & STUDIO PRESET pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "10-BAND EQ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )

            // Preset indicator pill: px-3 py-1 bg-cyan-500/10 text-cyan-400 rounded-full text-[10px] font-bold border border-cyan-500/20
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyanPillBg)
                    .border(1.dp, CyanPillBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = presetName.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = CyanGlow
                )
            }
        }

        // Smooth Spline Frequency Response Curve
        EqualizerCurveCanvas(
            bands = bands,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        // 10-Band Sliders Container: bg-slate-900/20 rounded-2xl p-2 border border-white/5
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ImmersiveInnerBg)
                .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(16.dp))
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val shortLabels = listOf("31", "62", "125", "250", "500", "1k", "2k", "4k", "8k", "16k")
                shortLabels.forEachIndexed { index, label ->
                    val levelDb = bands.getOrElse(index) { 0 }
                    VerticalBandSlider(
                        bandIndex = index,
                        frequencyLabel = label,
                        levelDb = levelDb,
                        onLevelChanged = { newDb -> onBandChanged(index, newDb) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Renders smooth spline frequency response curve based on current 10-band slider values.
 */
@Composable
private fun EqualizerCurveCanvas(
    bands: List<Int>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ImmersiveInnerBg)
            .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp)) {
            val width = size.width
            val height = size.height
            val midY = height / 2f

            // Zero dB baseline
            drawLine(
                color = CyanPrimary.copy(alpha = 0.15f),
                start = Offset(0f, midY),
                end = Offset(width, midY),
                strokeWidth = 1.dp.toPx()
            )

            val numPoints = bands.size
            if (numPoints < 2) return@Canvas

            val stepX = width / (numPoints - 1)
            val points = bands.mapIndexed { i, db ->
                val fractionY = (db.toFloat() / 15f).coerceIn(-1f, 1f)
                val y = midY - (fractionY * (height * 0.42f))
                val x = i * stepX
                Offset(x, y)
            }

            // Construct smooth spline path
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val cx = (p0.x + p1.x) / 2f
                    cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                }
            }

            // Area fill under spline
            val fillPath = Path().apply {
                addPath(path)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(CyanPrimary.copy(alpha = 0.20f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )

            // Sleek Cyan Stroke
            drawPath(
                path = path,
                color = CyanGlow,
                style = Stroke(width = 1.8.dp.toPx())
            )

            // Small glowing node points
            points.forEach { pt ->
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = CyanPrimary,
                    radius = 2.dp.toPx(),
                    center = pt,
                    style = Stroke(width = 0.8.dp.toPx())
                )
            }
        }
    }
}

/**
 * Individual vertical band slider matching Immersive UI:
 * - Track: w-1 h-28 bg-slate-800 rounded-full
 * - Knob: w-4 h-4 bg-white rounded-full shadow-lg border-2 border-cyan-500
 * - Label: text-[8px] text-slate-500 font-bold
 */
@Composable
private fun VerticalBandSlider(
    bandIndex: Int,
    frequencyLabel: String,
    levelDb: Int,
    onLevelChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .testTag("eq_band_$bandIndex"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Level text indicator
        Text(
            text = if (levelDb > 0) "+$levelDb" else "$levelDb",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (levelDb != 0) CyanGlow else TextMuted
        )

        // Vertical Slider Track
        Box(
            modifier = Modifier
                .width(24.dp)
                .weight(1f)
                .padding(vertical = 2.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val fraction = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                        val newDb = ((fraction * 30f) - 15f).toInt().coerceIn(-15, 15)
                        onLevelChanged(newDb)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        val fraction = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                        val newDb = ((fraction * 30f) - 15f).toInt().coerceIn(-15, 15)
                        onLevelChanged(newDb)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val centerX = width / 2f
                val trackWidth = 3.5.dp.toPx()
                val cornerRadius = CornerRadius(trackWidth / 2, trackWidth / 2)

                // Background track (bg-slate-800)
                drawRoundRect(
                    color = ImmersiveSlate800,
                    topLeft = Offset(centerX - trackWidth / 2, 0f),
                    size = Size(trackWidth, height),
                    cornerRadius = cornerRadius
                )

                // Thumb Y calculation
                val fraction = ((levelDb + 15f) / 30f).coerceIn(0f, 1f)
                val thumbY = height - (fraction * height)
                val midY = height / 2f

                // Active fill: bg-cyan-500/30
                val fillTopY = minOf(thumbY, midY)
                val fillHeight = kotlin.math.abs(thumbY - midY)
                if (fillHeight > 0f) {
                    drawRoundRect(
                        color = CyanPrimary.copy(alpha = 0.45f),
                        topLeft = Offset(centerX - trackWidth / 2, fillTopY),
                        size = Size(trackWidth, fillHeight),
                        cornerRadius = cornerRadius
                    )
                }

                // Thumb Knob: w-4 h-4 bg-white rounded-full shadow-lg border-2 border-cyan-500
                val thumbRadius = 6.5.dp.toPx()

                // Glow / shadow
                drawCircle(
                    color = CyanGlow.copy(alpha = if (isDragging) 0.5f else 0.25f),
                    radius = thumbRadius + 3.dp.toPx(),
                    center = Offset(centerX, thumbY)
                )

                // Solid white core
                drawCircle(
                    color = Color.White,
                    radius = thumbRadius,
                    center = Offset(centerX, thumbY)
                )

                // 2dp Cyan border
                drawCircle(
                    color = CyanPrimary,
                    radius = thumbRadius,
                    center = Offset(centerX, thumbY),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // Frequency Label: text-[8px] text-slate-500 font-bold
        Text(
            text = frequencyLabel,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = TextMuted
        )
    }
}
