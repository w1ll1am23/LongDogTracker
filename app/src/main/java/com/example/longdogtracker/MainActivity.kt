package com.example.longdogtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.longdogtracker.bottomnavigation.NavItem
import com.example.longdogtracker.bottomnavigation.NavigationGraph
import com.example.longdogtracker.bottomnavigation.BottomNavigation
import com.example.longdogtracker.ui.theme.LongDogTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Box(modifier = Modifier.safeDrawingPadding()) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                LongDogTrackerTheme {
                    Scaffold(
                        backgroundColor = MaterialTheme.colors.background,
                        topBar = {
                            TopAppBar(title = { Text("Long Dog Tracker") },
                                actions = if (navBackStackEntry?.destination?.route != NavItem.Settings.screenRoute) {
                                    {
                                        IconButton(onClick = {
                                            navController.navigate(NavItem.Settings.screenRoute) {
                                                navController.graph.startDestinationRoute?.let { screenRoute ->
                                                    popUpTo(screenRoute) {
                                                        saveState = true
                                                    }
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }) {
                                            Icon(imageVector = Icons.Default.Settings, null)
                                        }
                                    }
                                } else {
                                    {}
                                }
                            )
                        },
                        bottomBar = {
                            BottomNavigation(navController)
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .background(MaterialTheme.colors.background)
                                .padding(innerPadding)
                        ) {
                            NavigationGraph(navController = navController)
                        }
                    }
                }
            }
        }
    }
}