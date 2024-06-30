package com.example.longdogtracker.bottomnavigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.longdogtracker.features.characters.ui.CharactersScreen
import com.example.longdogtracker.features.episodes.ui.EpisodesScreen
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
            EpisodesScreen()
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