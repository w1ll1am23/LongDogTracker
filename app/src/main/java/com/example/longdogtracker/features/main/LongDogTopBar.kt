package com.example.longdogtracker.features.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.longdogtracker.R
import com.example.longdogtracker.bottomnavigation.NavItem
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongDogTopBar(navController: NavController, navBackStackEntry: NavBackStackEntry?) {
    TopAppBar(title = { Text("Long Dog Tracker") },
        actions = if (navBackStackEntry?.destination?.route != NavItem.Settings.screenRoute) {
            {
                LongDogTopBarActionView {
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
        } else {
            {}
        }
    )
}

@Composable
private fun LongDogTopBarActionView(navigate: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = {
        expanded = true
    }) {
        Icon(imageVector = Icons.Default.MoreVert, null)
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_filter_sort)) },
            onClick = { }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_settings)) },
            onClick = { navigate.invoke() }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LongDogTopBarActionViewPreview() {
    LongDogTrackerPrimaryTheme {
        LongDogTopBarActionView {

        }
    }
}