package com.example.longdogtracker.features.media.ui.model

import androidx.annotation.StringRes

sealed class MediaUIState {
    data object Loading : MediaUIState()
    data class Media(
        val items: List<MediaListItem>,
        val episodeLocations: Map<String, Int>,
    ) : MediaUIState()

    data class Error(@StringRes val errorMessage: Int) : MediaUIState()
}

sealed class MediaListItem {
    data class Header(val key: Int, val index: Int, val season: String, val longDogs: String, val totalCount: String, val firstEpisodeIndex: Int, val lastEpisodeIndex: Int) : MediaListItem()
    data class Media(val key: Int, val index: Int, val media: UiEpisode) : MediaListItem()

    fun uniqueKey(): Int {
        return when (this) {
            is Header -> this.key
            is Media -> this.key
        }
    }
}