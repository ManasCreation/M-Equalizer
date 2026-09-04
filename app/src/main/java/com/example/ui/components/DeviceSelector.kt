package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceType
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPillBg
import com.example.ui.theme.CyanPillBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ImmersiveBorderSubtle
import com.example.ui.theme.ImmersiveCardBgTranslucent
import com.example.ui.theme.ImmersiveInnerBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DeviceSelector(
    selectedDeviceType: DeviceType,
    onDeviceSelected: (DeviceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(ImmersiveCardBgTranslucent)
            .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("device_selector_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DEVICE PROFILE MEMORY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = CyanGlow,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "AUTO-SYNCED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = CyanGlow
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val devices = listOf(
                Triple(DeviceType.HEADPHONES, "Headphones", Icons.Default.Headphones),
                Triple(DeviceType.EARBUDS, "Earbuds", Icons.Default.Hearing),
                Triple(DeviceType.CAR_STEREO, "Car Stereo", Icons.Default.DirectionsCar),
                Triple(DeviceType.SPEAKERS, "Speakers", Icons.Default.Speaker)
            )

            devices.forEach { (type, label, icon) ->
                val isSelected = selectedDeviceType == type
                val borderGlow by animateColorAsState(
                    targetValue = if (isSelected) CyanGlow else ImmersiveBorderSubtle,
                    animationSpec = tween(150),
                    label = "device_border_glow"
                )
                val bgTint by animateColorAsState(
                    targetValue = if (isSelected) CyanPillBg else ImmersiveInnerBg,
                    animationSpec = tween(150),
                    label = "device_bg_tint"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgTint)
                        .border(
                            width = 1.dp,
                            color = borderGlow,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onDeviceSelected(type) }
                        .padding(vertical = 12.dp, horizontal = 4.dp)
                        .testTag("device_tab_${type.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) CyanGlow else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
