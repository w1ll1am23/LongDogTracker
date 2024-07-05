package com.example.longdogtracker.features.settings

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("SettingsPreferences", Context.MODE_PRIVATE)

    fun readBooleanPreference(pref: String): Boolean {
        return preferences.getBoolean(pref, false)
    }

    fun writeBooleanPreference(pref: String, value: Boolean) {
        with(preferences.edit()) {
            putBoolean(pref, value)
            apply()
        }
    }

    fun writeIntListPreference(pref: String, value: List<Int>) {
        with(preferences.edit()) {
            putStringSet(pref, value.map { it.toString() }.toSet())
            apply()
        }
    }

    fun readIntListPreference(pref: String): List<Int>? {
        return preferences.getStringSet(pref, null)?.let { stringSet ->
            stringSet.map { it.toInt() }
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

    fun writeLongPreference(pref: String, value: Long) {
        with(preferences.edit()) {
            putLong(pref, value)
            apply()
        }
    }

    fun readLongPreference(pref: String): Long? {
        val longPref = preferences.getLong(pref, 0L)
        return if (longPref == 0L) {
            null
        } else {
            longPref
        }
    }

    fun deletePreference(pref: String) {
        with(preferences.edit()) {
            remove(pref)
            apply()
        }
    }
}