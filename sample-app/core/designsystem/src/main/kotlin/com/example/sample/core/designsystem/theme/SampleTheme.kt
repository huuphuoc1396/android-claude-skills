package com.example.sample.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** The app is dark only. */
private val DarkColors = darkColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Pink,
    onSecondary = Color.White,
    background = Night,
    onBackground = Color.White,
    surface = Slate,
    onSurface = Color.White,
    surfaceVariant = Slate,
    onSurfaceVariant = TextSecondary,
)

@Composable
fun SampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = SampleTypography,
        content = content,
    )
}
