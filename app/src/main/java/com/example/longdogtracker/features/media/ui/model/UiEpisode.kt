package com.example.longdogtracker.features.media.ui.model

data class UiEpisode(
    val id: Int,
    val apiId: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val knownLongDogCount: Int,
    val longDogsFound: Int,
    val seasonEpisode: String,
    val season: String,
    val episode: String,
    val longDogLocations: List<UiLongDogLocation>?
)
