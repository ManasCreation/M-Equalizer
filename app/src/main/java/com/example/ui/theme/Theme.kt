package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = NeonCyan,
    secondary = NeonViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF261054),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary = NeonMagenta,
    onTertiary = Color.White,
    background = CyberDarkBg,
    onBackground = TextPrimaryDark,
    surface = CyberCardBg,
    onSurface = TextPrimaryDark,
    surfaceVariant = CyberCardElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = CyberBorderStroke,
    outlineVariant = CyberBorderGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}

