package com.example.longdogtracker.features.media.ui.model

data class UiEpisode(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val season: Int,
    val episode: Int,
    val knownLongDogCount: Int,
    val longDogsFound: Int,
    val longDogLocation: String?
)
