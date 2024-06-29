package com.example.longdogtracker.features.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = stringResource(id = item.title()),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                                Box(
                                    modifier = Modifier.weight(0.25f),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    when (item) {
                                        is UiSetting.RangeSetting -> Unit
                                        is UiSetting.ResetSetting -> {
                                            ResetSetting(item)
                                        }
                                        is UiSetting.ToggleSetting -> {
                                            OnOffSetting(item)
                                        }
                                    }
                                }
                            }
                            Text(
                                text = stringResource(id = item.description()),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            if (item is UiSetting.RangeSetting) {
                                Spacer(modifier = Modifier.size(4.dp))
                                Box(modifier = Modifier.padding(8.dp)) {
                                }
                            }
                        }
                        if (index < state.uiSettings.size - 1) {
                            Divider()
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
private fun ResetSetting(setting: UiSetting.ResetSetting) {
    Button(onClick = { setting.updateSetting(setting.id) }) {
        Text(stringResource(id = R.string.setting_reset_button))
    }
}