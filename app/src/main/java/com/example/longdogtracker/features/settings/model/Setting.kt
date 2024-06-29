package com.example.longdogtracker.features.settings.model

import androidx.annotation.StringRes

data class Setting(
    val id: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
    val type: SettingType = SettingType.ON_OFF,
)
