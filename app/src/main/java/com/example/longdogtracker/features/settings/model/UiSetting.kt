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

    data class RangeSetting(
        val id: String,
        @StringRes val title: Int,
        @StringRes val description: Int,
        val range: Pair<Int, Int>?,
        val updateSetting: (id: String, range: Pair<Int, Int>) -> Unit
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
            is RangeSetting -> this.id
            is ResetSetting -> this.id
        }
    }

    fun title(): Int {
        return when (this) {
            is ToggleSetting -> this.title
            is RangeSetting -> this.title
            is ResetSetting -> this.title
        }
    }

    fun description(): Int {
        return when (this) {
            is ToggleSetting -> this.description
            is RangeSetting -> this.description
            is ResetSetting -> this.description
        }
    }
}
