package com.example.longdogtracker.features.episodes.ui.model

import androidx.annotation.StringRes

sealed class EpisodesUIState {
    data object Loading : EpisodesUIState()
    data class Episodes(
        val seasonEpisodeMap: Map<UiSeason, List<UiEpisode>>,
    ) : EpisodesUIState()
    data class Error(@StringRes val errorMessage: Int): EpisodesUIState()
}