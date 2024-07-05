package com.example.longdogtracker.features.media.ui.model

sealed class EpisodeFilterSheetUIState {
    data object Loading : EpisodeFilterSheetUIState()
    data class Filters(
        val seasons: List<Season>,
        val showMovies: Boolean,
    ) : EpisodeFilterSheetUIState()
}


data class Season(val number: Int, val selected: Boolean)