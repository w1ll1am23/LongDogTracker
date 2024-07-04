package com.example.longdogtracker.features.episodes.ui.model

sealed class EpisodeFilterSheetUIState {
    data object Loading : EpisodeFilterSheetUIState()
    data class Filters(
        val seasons: List<Season>,
    ) : EpisodeFilterSheetUIState()
}


data class Season(val number: Int, val selected: Boolean)