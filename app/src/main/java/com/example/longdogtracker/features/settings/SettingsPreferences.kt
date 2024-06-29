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

    fun readStringPreference(pref: String): String? {
        return preferences.getString(pref, null)
    }

    fun writeStringPreference(pref: String, value: String) {
        with(preferences.edit()) {
            putString(pref, value)
            apply()
        }
    }
}