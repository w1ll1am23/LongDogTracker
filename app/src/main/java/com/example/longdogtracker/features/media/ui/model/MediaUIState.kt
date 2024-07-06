package com.example.longdogtracker.features.media.ui.model

import androidx.annotation.StringRes

sealed class MediaUIState {
    data object Loading : MediaUIState()
    data class Media(
        val seasonEpisodeMap: Map<UiSeason, List<UiMedia>>,
        val movies: List<UiMedia>,
        val books: List<UiMedia>
    ) : MediaUIState()
    data class Error(@StringRes val errorMessage: Int): MediaUIState()
}