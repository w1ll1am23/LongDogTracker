package com.example.longdogtracker.features.settings.model

import androidx.annotation.StringRes

sealed class SettingType {
    data object OnOff : SettingType()
    data object StringValue: SettingType()
    data class Action(val action: ActionType, @StringRes val actionCopy: Int): SettingType()
}

enum class ActionType {
    BACKUP,
    RESET
}