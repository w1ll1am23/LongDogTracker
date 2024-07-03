package com.example.longdogtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BingoColorPalette = lightColorScheme(
    background = BingoBodyPrimary,
    primary = BingoBodyAccentDark,
    onPrimaryContainer = Color.Red,
)

private val BlueyColorScheme = lightColorScheme(
    primary = BlueyBodyAccentDark, // Button background color
    onPrimary = BlueyBodyAccentLight, // Button text color
    primaryContainer = Color.Green,
    onPrimaryContainer = Color.Green,
    inversePrimary = Color.Cyan,
    secondary = Color.Magenta,
    onSecondary = Color.White,
    secondaryContainer = BlueyBodyAccentDark, // Nav item color
    onSecondaryContainer = BlueyBodyPrimary, // Nav item icon color unselected
    tertiary = Color.LightGray,
    onTertiary = Color.Red,
    tertiaryContainer = Color.Red,
    onTertiaryContainer = Color.Red,
    background = BlueyBackgroundPrimary, // The main app background
    onBackground = BlueyBodyAccentDark, // Text directly on the main background
    surface = AppBarBackground, // Top bar and bottom nav background color (bottom sheet background)
    onSurface = BlueyBodyAccentDark, // Top bar and bottom nav text color
    surfaceVariant = BlueyBodyAccentLight, // Card background
    onSurfaceVariant = BlueyBodyAccentDark, // Text on card, unselected icons in top bar and bottom nav
    outline = BlueyBodyAccentDark,
    outlineVariant = BlueyBodyAccentDark, // Divider on main background
    scrim = BlueyBodyAccentDark,
    surfaceBright = Color.Red,
    surfaceContainer = Color.Red,
    surfaceContainerHigh = Color.Red,
    surfaceContainerHighest = Color.Red,
    surfaceContainerLow = Color.Red,
    surfaceContainerLowest = Color.Red,
    surfaceDim = Color.Red,
)

@Composable
fun LongDogTrackerPrimaryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlueyColorScheme,
        content = content
    )
}