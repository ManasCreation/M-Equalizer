package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorderStroke
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark

@Composable
fun GlowingHorizontalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    title: String,
    valueLabel: String,
    accentColor: Color = NeonCyan,
    modifier: Modifier = Modifier,
    testTag: String = "slider_$title"
) {
    var isDragging by remember { androidx.compose.runtime.mutableStateOf(false) }

    val trackColor = CyberBorderStroke
    val glowColor by animateColorAsState(
        targetValue = if (isDragging) accentColor else accentColor.copy(alpha = 0.6f),
        animationSpec = tween(150),
        label = "glow_anim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextPrimaryDark
            )
            Text(
                text = valueLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = accentColor
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .pointerInput(valueRange) {
                    detectTapGestures { offset ->
                        val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                        val newValue = valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                    }
                }
                .pointerInput(valueRange) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        val newValue = valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val trackHeight = 6.dp.toPx()
                val cornerRadius = CornerRadius(trackHeight / 2, trackHeight / 2)
                val trackY = (canvasHeight - trackHeight) / 2

                // Background track
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(0f, trackY),
                    size = Size(canvasWidth, trackHeight),
                    cornerRadius = cornerRadius
                )

                // Active progress track
                val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
                val activeWidth = canvasWidth * fraction

                if (activeWidth > 0f) {
                    // Glowing active fill
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.5f), accentColor),
                        startX = 0f,
                        endX = activeWidth
                    )
                    drawRoundRect(
                        brush = gradient,
                        topLeft = Offset(0f, trackY),
                        size = Size(activeWidth, trackHeight),
                        cornerRadius = cornerRadius
                    )

                    // Outer glow halo along the track
                    drawRoundRect(
                        color = glowColor.copy(alpha = 0.25f),
                        topLeft = Offset(0f, trackY - 2.dp.toPx()),
                        size = Size(activeWidth, trackHeight + 4.dp.toPx()),
                        cornerRadius = CornerRadius((trackHeight + 4.dp.toPx()) / 2)
                    )
                }

                // Thumb handle
                val thumbX = activeWidth.coerceIn(8.dp.toPx(), canvasWidth - 8.dp.toPx())
                val thumbRadius = if (isDragging) 9.dp.toPx() else 7.5.dp.toPx()

                // Thumb glow
                drawCircle(
                    color = glowColor.copy(alpha = 0.4f),
                    radius = thumbRadius + 5.dp.toPx(),
                    center = Offset(thumbX, canvasHeight / 2)
                )

                // Thumb solid core
                drawCircle(
                    color = Color.White,
                    radius = thumbRadius,
                    center = Offset(thumbX, canvasHeight / 2)
                )

                // Thumb border
                drawCircle(
                    color = accentColor,
                    radius = thumbRadius,
                    center = Offset(thumbX, canvasHeight / 2),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }
        }
    }
}
