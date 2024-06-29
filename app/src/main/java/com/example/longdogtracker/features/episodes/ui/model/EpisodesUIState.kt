package com.example.longdogtracker.features.episodes.ui.model

sealed class EpisodesUIState {
    data object Loading : EpisodesUIState()
    data class Episodes(
        val seasonEpisodeMap: Map<UiSeason, List<UiEpisode>>,
    ) : EpisodesUIState()
}