package com.example.longdogtracker.features.main

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.longdogtracker.R
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongDogTopBar(navigate: (TopBarNavigation) -> Unit) {
    TopAppBar(title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            LongDogTopBarActionView { topBarNavigation ->
                navigate.invoke(topBarNavigation)
            }
        }
    )
}

@Composable
private fun LongDogTopBarActionView(navigate: (TopBarNavigation) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row {
        IconButton(onClick = {
            navigate(TopBarNavigation.SEARCH)
        }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }
        IconButton(onClick = {
            navigate(TopBarNavigation.FILTER)
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
            onClick = { navigate(TopBarNavigation.SETTINGS) }
        )
    }
}

enum class TopBarNavigation {
    SETTINGS,
    FILTER,
    SEARCH
}

@Preview(showBackground = true)
@Composable
private fun LongDogTopBarActionViewPreview() {
    LongDogTrackerPrimaryTheme {
        LongDogTopBarActionView {

        }
    }
}