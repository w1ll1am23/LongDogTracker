package com.example.longdogtracker.features.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreferences @Inject constructor(@ApplicationContext context: Context) {
    val preferences = context.getSharedPreferences("SettingsPreferences", Context.MODE_PRIVATE)

    fun readBooleanPreference(pref: String): Boolean {
        return preferences.getBoolean(pref, false)
    }

    fun writeBooleanPreference(pref: String, value: Boolean) {
        with(preferences.edit()) {
            putBoolean(pref, value)
            apply()
        }
    }

    fun readIntRange(pref: String): Pair<Int, Int>? {
        val rangeString = preferences.getString(pref, null)
        val startAndEnd = rangeString?.split("-")
        return startAndEnd?.let {
            Pair(it.first().toInt(), it.last().toInt())
        }
    }

    fun writeIntRange(pref: String, range: Pair<Int, Int>) {
        with(preferences.edit()) {
            putString(pref, "${range.first}-${range.second}")
            apply()
        }
    }

    fun resetPreference(pref: String) {
        with(preferences.edit()) {
            remove(pref)
            apply()
        }
    }
}