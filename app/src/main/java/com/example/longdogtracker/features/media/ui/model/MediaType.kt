package com.example.longdogtracker.features.media.ui.model

sealed class MediaType {
    data class Show(val episode: Int, val season: Int): MediaType()
    data object Movie : MediaType()
    data object Book : MediaType()
}