package com.example.longdogtracker.features.settings.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPerfs: SettingsPreferences,
) :
    ViewModel() {

    private val settingsMutableStateFlow =
        MutableStateFlow<SettingsState>(SettingsState.Loading)
    val settingsStateFlow: StateFlow<SettingsState> = settingsMutableStateFlow

    fun loadSettings() {
        settingsMutableStateFlow.value = SettingsState.Loading
        viewModelScope.launch {
            val allUiSettings = mutableListOf<UiSetting>()
            allSettingsList.forEach { setting ->
                allUiSettings.add(
                    when (setting.type) {
                        SettingType.ON_OFF -> {
                            UiSetting.ToggleSetting(
                                setting.id,
                                setting.title,
                                setting.description,
                                settingsPerfs.readBooleanPreference(setting.id),
                                ::updateToggleSetting
                            )
                        }
                        SettingType.STRING -> {
                            UiSetting.StringSetting(
                                setting.id,
                                setting.title,
                                setting.description,
                                settingsPerfs.readStringPreference(setting.id),
                                ::updateStringSetting
                            )
                        }
                        SettingType.RESET -> {
                            UiSetting.ResetSetting(
                                setting.id,
                                setting.title,
                                setting.description,
                                {}
                            )
                        }
                    }
                )
            }
            settingsMutableStateFlow.value = SettingsState.Settings(allUiSettings)
        }
    }

    private fun updateToggleSetting(id: String, toggledOn: Boolean) {
        Log.d("SettingsViewModel", "Updating $id to $toggledOn")
        settingsPerfs.writeBooleanPreference(id, toggledOn)
    }

    private fun updateStringSetting(id: String, value: String?) {
        Log.d("SettingsViewModel", "Updating $id to $value")
        value?.let {
            settingsPerfs.writeStringPreference(id, it)
        }
    }


}