package com.example.longdogtracker.network

import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingOauthToken
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AddAuthInterceptor @Inject constructor(private val preferences: SettingsPreferences) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(preferences.readStringPreference(
            settingOauthToken
        )?.let {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $it").build()
        } ?: run {
            chain.request()
        })
    }


}