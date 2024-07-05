package com.example.longdogtracker.features.media.ui.model

import androidx.annotation.StringRes

sealed class MediaUIState {
    data object Loading : MediaUIState()
    data class Media(
        val seasonEpisodeMap: Map<UiSeason, List<UiEpisode>>,
        val movies: List<UiMovie>,
    ) : MediaUIState()
    data class Error(@StringRes val errorMessage: Int): MediaUIState()
}