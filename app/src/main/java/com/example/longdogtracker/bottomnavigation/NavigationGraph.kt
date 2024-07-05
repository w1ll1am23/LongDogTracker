package com.example.longdogtracker.bottomnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.longdogtracker.features.characters.ui.CharactersScreen
import com.example.longdogtracker.features.media.ui.MediaScreen
import com.example.longdogtracker.features.settings.ui.SettingsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
) {
    NavHost(navController, startDestination = NavItem.Episodes.screenRoute) {
        composable(NavItem.Episodes.screenRoute, enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(600)
            )
        }, exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(600)
            )
        }) {
            MediaScreen {
                navController.navigate(NavItem.Settings.screenRoute) {
                    navController.graph.startDestinationRoute?.let { screenRoute ->
                        popUpTo(screenRoute) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        composable(NavItem.Characters.screenRoute, enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(600)
            )
        }, exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(600)
            )
        }) {
            CharactersScreen()
        }

        composable(NavItem.Settings.screenRoute, enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Up,
                animationSpec = tween(600)
            )
        }, exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Down,
                animationSpec = tween(600)
            )
        }) {
            SettingsScreen()
        }
    }
}