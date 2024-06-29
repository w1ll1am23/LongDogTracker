package com.example.longdogtracker.features.settings.model

import androidx.annotation.StringRes

sealed class UiSetting {
    data class ToggleSetting(
        val id: String,
        @StringRes val title: Int,
        @StringRes val description: Int,
        val currentState: Boolean,
        val updateSetting: (id: String, toggledOn: Boolean) -> Unit
    ) : UiSetting()

    data class StringSetting(
        val id: String,
        @StringRes val title: Int,
        @StringRes val description: Int,
        val value: String?,
        val updateSetting: (id: String, value: String?) -> Unit
    ) : UiSetting()

    data class ResetSetting(
        val id: String,
        @StringRes val title: Int,
        @StringRes val description: Int,
        val updateSetting: (id: String) -> Unit
    ) : UiSetting()

    fun id(): String {
        return when (this) {
            is ToggleSetting -> this.id
            is StringSetting -> this.id
            is ResetSetting -> this.id
        }
    }

    fun title(): Int {
        return when (this) {
            is ToggleSetting -> this.title
            is StringSetting -> this.title
            is ResetSetting -> this.title
        }
    }

    fun description(): Int {
        return when (this) {
            is ToggleSetting -> this.description
            is StringSetting -> this.description
            is ResetSetting -> this.description
        }
    }
}
