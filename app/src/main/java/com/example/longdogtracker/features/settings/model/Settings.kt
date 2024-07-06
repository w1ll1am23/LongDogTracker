package com.example.longdogtracker.features.settings.model

import com.example.longdogtracker.R

const val settingOauthToken = "OAUTH_TOKEN"
const val settingResetCache = "RESET_CACHE"
const val settingSeasonFilter = "SEASON_FILTER"
const val settingMovieFilter = "MOVIE_FILTER"
const val settingBooksFilter = "BOOKS_FILTER"
const val settingLastFetchSeasonsFromService = "LAST_FETCH_SEASONS_FROM_SERVICE"
const val settingLastFetchEpisodesFromService = "LAST_FETCH_EPISODES_FROM_SERVICE"
const val settingLastFetchMoviesFromService = "LAST_FETCH_MOVIES_FROM_SERVICE"
const val settingLastFetchBooksFromService = "LAST_FETCH_BOOKS_FROM_SERVICE"




val oauthToken = Setting(
    id = settingOauthToken,
    title = R.string.setting_auth_token,
    description = R.string.setting_auth_token_description,
    type = SettingType.STRING
)

val resetCache = Setting(
    id = settingResetCache,
    title = R.string.setting_reset_cache,
    description = R.string.setting_reset_cache_description,
    type = SettingType.RESET
)

val allSettingsList = listOf(oauthToken, resetCache)