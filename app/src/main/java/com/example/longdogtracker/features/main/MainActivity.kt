package com.example.longdogtracker.features.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.longdogtracker.bottomnavigation.MainBottomNavigation
import com.example.longdogtracker.bottomnavigation.NavigationGraph
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LongDogTrackerPrimaryTheme {
                Box(modifier = Modifier.safeDrawingPadding()) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    Scaffold(
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