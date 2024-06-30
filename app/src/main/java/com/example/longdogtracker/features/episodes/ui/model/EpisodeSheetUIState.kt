package com.example.longdogtracker.features.episodes.ui.model

sealed class EpisodeSheetUIState {
    data object Loading : EpisodeSheetUIState()
    data class Episode(
        val seasonEpisodeMap: Map<UiSeason, List<UiEpisode>>,
    ) : EpisodeSheetUIState()
}