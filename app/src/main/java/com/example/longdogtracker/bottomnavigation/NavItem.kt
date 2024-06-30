package com.example.longdogtracker.bottomnavigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.longdogtracker.R


sealed class NavItem(@StringRes var title: Int, var icon: ImageVector, var screenRoute: String) {

    data object Episodes : NavItem(
        title = R.string.navigation_episodes,
        icon = Icons.AutoMirrored.Default.List,
        screenRoute = "episodes"
    )

    data object Characters : NavItem(
        title = R.string.navigation_characters,
        icon = Icons.Default.Face,
        screenRoute = "characters"
    )

    data object Settings : NavItem(
        title = R.string.navigation_settings,
        icon = Icons.Default.Settings,
        screenRoute = "settings"
    )
}
