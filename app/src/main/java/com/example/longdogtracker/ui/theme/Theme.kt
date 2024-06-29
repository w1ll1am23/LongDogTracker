package com.example.longdogtracker.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val BlueyColorPalette = lightColors(
    background = BlueyBackgroundPrimary,
    surface = BlueyBodyAccentLight,
    primary = BlueyBodyAccentDark,
    secondary = BlueyBodyAccentDark
)

@Composable
fun LongDogTrackerTheme(content: @Composable () -> Unit) {

    MaterialTheme(
        colors = BlueyColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}