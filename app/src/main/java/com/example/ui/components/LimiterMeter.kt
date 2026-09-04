package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPillBg
import com.example.ui.theme.CyanPillBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ImmersiveBorderSubtle
import com.example.ui.theme.ImmersiveCardBgTranslucent
import com.example.ui.theme.ImmersiveInnerBg
import com.example.ui.theme.ImmersiveSlate800
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LimiterMeter(
    limiterEnabled: Boolean,
    onLimiterToggled: (Boolean) -> Unit,
    agcEnabled: Boolean,
    onAgcToggled: (Boolean) -> Unit,
    reductionDb: Float,
    peakOutputDb: Float,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = if (limiterEnabled && agcEnabled) CyanGlow else if (limiterEnabled) CyanPrimary else NeonAmber,
        label = "status_color"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ImmersiveCardBgTranslucent)
            .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("anti_clipping_module"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .border(1.dp, statusColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Anti-Clipping Security",
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ANTI-CLIPPING DYNAMICS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = if (limiterEnabled) "Brickwall Limiter Guard Active" else "Limiter Bypassed",
                        fontSize = 11.sp,
                        color = if (limiterEnabled) CyanGlow else NeonAmber
                    )
                }
            }

            // Studio Master Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyanPillBg)
                    .border(1.dp, CyanPillBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "STUDIO PURITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = CyanGlow
                )
            }
        }

        // Dynamic Headroom & Reduction Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ImmersiveInnerBg)
                .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "HEADROOM ATTENUATION (AGC)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
                Text(
                    text = if (reductionDb > 0f) String.format("-%.1f dB", reductionDb) else "0.0 dB (CLEAN)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (reductionDb > 0f) CyanGlow else TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress meter for headroom protection
            val meterFraction = (reductionDb / 12f).coerceIn(0.04f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(ImmersiveSlate800)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = meterFraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(CyanPrimary, CyanGlow)
                            )
                        )
                )
            }
        }

        // Dual Toggles: Brickwall Limiter & Dynamic AGC
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Limiter Card
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ImmersiveInnerBg)
                    .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(16.dp))
                    .clickable { onLimiterToggled(!limiterEnabled) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BRICKWALL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "-0.5 dB Clamp",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }
                Switch(
                    checked = limiterEnabled,
                    onCheckedChange = onLimiterToggled,
                    modifier = Modifier.size(36.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = CyanPrimary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = ImmersiveSlate800
                    )
                )
            }

            // AGC Card
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ImmersiveInnerBg)
                    .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(16.dp))
                    .clickable { onAgcToggled(!agcEnabled) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AUTO GAIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Zero Distortion",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }
                Switch(
                    checked = agcEnabled,
                    onCheckedChange = onAgcToggled,
                    modifier = Modifier.size(36.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = CyanPrimary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = ImmersiveSlate800
                    )
                )
            }
        }
    }
}
