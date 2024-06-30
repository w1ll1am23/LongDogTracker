package com.example.longdogtracker.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.R
import com.example.longdogtracker.features.settings.model.SettingsState
import com.example.longdogtracker.features.settings.model.UiSetting
import com.example.longdogtracker.features.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val viewState by viewModel.settingsStateFlow.collectAsState()
    LaunchedEffect(key1 = null) {
        viewModel.loadSettings()
    }
    HandleSettingsState(state = viewState)
}

@Composable
private fun HandleSettingsState(state: SettingsState) {
    when (state) {
        SettingsState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is SettingsState.Settings -> {
            LazyColumn {
                state.uiSettings.forEachIndexed { index, item ->
                    item(key = item.id(), content = {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = item.title()),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Text(
                                text = stringResource(id = item.description()),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            when (item) {
                                is UiSetting.ResetSetting -> {
                                    ResetSetting(item)
                                }
                                is UiSetting.StringSetting -> {
                                    StringSetting(item)
                                }

                                is UiSetting.ToggleSetting -> Unit
                            }
                        }
                        if (index < state.uiSettings.size - 1) {
                            HorizontalDivider()
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun OnOffSetting(setting: UiSetting.ToggleSetting) {
    val checkedState = remember { mutableStateOf(setting.currentState) }
    Switch(
        checked = checkedState.value,
        onCheckedChange = { togged ->
            checkedState.value = togged
            setting.updateSetting.invoke(setting.id, togged)
        })
}

@Composable
private fun StringSetting(setting: UiSetting.StringSetting) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        var text by remember { mutableStateOf(setting.value ?: "") }

        TextField(
            value = text,
            modifier = Modifier.weight(.7f),
            onValueChange = { text = it },
            maxLines = 1,
            label = { Text(stringResource(id = setting.description)) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { setting.updateSetting(setting.id, text) }, modifier = Modifier.weight(.3f)) {
            Text(stringResource(id = R.string.setting_save_button))
        }
    }

}

@Composable
private fun ResetSetting(setting: UiSetting.ResetSetting) {
    Button(onClick = { setting.updateSetting.invoke(setting.id) }) {
        Text(stringResource(id = R.string.setting_clear_cache_button))
    }

}