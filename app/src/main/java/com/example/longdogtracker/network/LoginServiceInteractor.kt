package com.example.longdogtracker.network

import androidx.annotation.StringRes
import com.example.longdogtracker.BuildConfig
import com.example.longdogtracker.R
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
import com.example.longdogtracker.features.episodes.network.model.TheTvDbLoginBody
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingOauthToken
import javax.inject.Inject

class LoginServiceInteractor @Inject constructor(
    private val theTvDbApi: TheTvDbApi,
    private val settingsPreferences: SettingsPreferences,
) {

    suspend fun isNotLoggedIn() =
        settingsPreferences.readStringPreference(settingOauthToken) == null

    suspend fun login(): LoginStatus {
        val result =
            theTvDbApi.getOauthToken(TheTvDbLoginBody(BuildConfig.THE_TV_DB_API_KEY)).execute()
        return if (result.isSuccessful) {
            result.body()?.data?.token?.let {
                settingsPreferences.writeStringPreference(settingOauthToken, it)
                LoginStatus.Success
            } ?: run {
                LoginStatus.Error(result.code(), R.string.error_unknown_issue_fetching_auth)
            }
        } else {
            LoginStatus.Error(result.code(), R.string.error_unknown_issue_fetching_auth)

        }
    }

    sealed class LoginStatus {
        data object Success : LoginStatus()
        data class Error(val statusCode: Int, @StringRes val errorMessage: Int) : LoginStatus()
    }
}