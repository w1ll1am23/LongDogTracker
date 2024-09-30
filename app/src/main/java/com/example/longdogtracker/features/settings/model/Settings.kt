package com.example.longdogtracker.features.settings.model

import com.example.longdogtracker.R

const val settingOauthToken = "OAUTH_TOKEN"
const val settingBackup = "BACKUP"
const val settingReset = "RESET"
const val settingSeasonFilter = "SEASON_FILTER"
const val settingFilterFound = "FOUND_FILTER"
const val settingFilterUnknown = "UNKNOWN_FILTER"
const val settingLastFetchSeasonsFromService = "LAST_FETCH_SEASONS_FROM_SERVICE"
const val settingLastFetchEpisodesFromService = "LAST_FETCH_EPISODES_FROM_SERVICE"

val backup = Setting(
    id = settingBackup,
    title = R.string.setting_backup,
    description = R.string.setting_backup_description,
    type = SettingType.Action(ActionType.BACKUP, R.string.setting_backup)
)

val reset = Setting(
    id = settingReset,
    title = R.string.setting_reset,
    description = R.string.setting_reset_description,
    type = SettingType.Action(ActionType.RESET, R.string.setting_reset)
)

val allSettingsList = listOf(backup, reset)