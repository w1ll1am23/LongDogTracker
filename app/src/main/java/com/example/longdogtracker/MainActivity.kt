package com.example.longdogtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.longdogtracker.bottomnavigation.NavItem
import com.example.longdogtracker.bottomnavigation.NavigationGraph
import com.example.longdogtracker.bottomnavigation.MainBottomNavigation
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LongDogTrackerPrimaryTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {

                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()

                    Scaffold(
                        topBar = {
                            LongDogTrackerPrimaryTheme {
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
                                                Icon(imageVector = Icons.Default.MoreVert, null)
                                            }
                                        }
                                    } else {
                                        {}
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            LongDogTrackerPrimaryTheme {
                                MainBottomNavigation(navController)
                            }
                        }
                    ) { innerPadding ->
                        LongDogTrackerPrimaryTheme {
                            Column(
                                modifier = Modifier
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
}