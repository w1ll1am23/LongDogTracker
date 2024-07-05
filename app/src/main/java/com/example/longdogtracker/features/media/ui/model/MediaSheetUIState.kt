package com.example.longdogtracker.features.media.ui.model

sealed class MediaSheetUIState {
    data object Loading : MediaSheetUIState()
    data class Media(
        val seasonEpisodeMap: Map<UiSeason, List<UiEpisode>>,
    ) : MediaSheetUIState()
}