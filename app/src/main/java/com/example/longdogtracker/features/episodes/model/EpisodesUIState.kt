package com.example.longdogtracker.features.episodes.model

sealed class EpisodesUIState {
    data object Loading : EpisodesUIState()
    data class Episodes(
        val episodes: List<UiEpisode>,
    ) : EpisodesUIState()
}