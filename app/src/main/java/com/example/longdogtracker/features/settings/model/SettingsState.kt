package com.example.longdogtracker.features.settings.model

sealed class SettingsState {
    object Loading : SettingsState()
    data class Settings(val uiSettings: List<UiSetting>) : SettingsState()
}