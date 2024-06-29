package com.example.longdogtracker.features.settings.model

import com.example.longdogtracker.R

const val settingApiKey = "API_KEY"
const val settingOauthToken = "OAUTH_TOKEN"


val apiKey = Setting(
    id = settingApiKey,
    title = R.string.setting_api_key,
    description = R.string.setting_api_key_description,
    type = SettingType.STRING
)

val oauthToken = Setting(
    id = settingOauthToken,
    title = R.string.setting_auth_token,
    description = R.string.setting_auth_token_description,
    type = SettingType.STRING
)


val allSettingsList = listOf(apiKey, oauthToken)