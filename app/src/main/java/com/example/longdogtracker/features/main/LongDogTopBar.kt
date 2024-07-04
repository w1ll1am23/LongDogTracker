package com.example.longdogtracker.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.longdogtracker.R
import com.example.longdogtracker.bottomnavigation.NavItem
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongDogTopBar(navController: NavController, navBackStackEntry: NavBackStackEntry?) {
    TopAppBar(title = {
        var query by remember {
            mutableStateOf("")
        }

        OutlinedTextField(
            value = query,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { query = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                }
            },
            onValueChange = { newQuery: String -> query = newQuery },
            modifier = Modifier.padding(end = 8.dp),
            shape = RoundedCornerShape(64.dp),
            singleLine = true
        )
    },
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
    Row {
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(painter = painterResource(id = R.drawable.filter), null)
        }
        IconButton(onClick = {
            expanded = true
        }) {
            Icon(imageVector = Icons.Default.MoreVert, null)
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
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