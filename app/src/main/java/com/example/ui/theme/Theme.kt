package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = NavyDark,
    primaryContainer = NavyCard,
    onPrimaryContainer = GoldBright,
    secondary = GoldSecondary,
    onSecondary = NavyDark,
    secondaryContainer = NavyLight,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = GoldBright,
    onTertiary = NavyDark,
    background = NavyDark,
    onBackground = TextPrimaryDark,
    surface = NavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = NavyBorder,
    outlineVariant = NavyLight,
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = NavyCard,
    onPrimaryContainer = GoldBright,
    secondary = GoldPrimary,
    onSecondary = NavyDark,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = NavyPrimaryLight,
    tertiary = GoldBright,
    onTertiary = NavyDark,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to institute brand dark navy & gold
    dynamicColor: Boolean = false, // Keep consistent academy branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
