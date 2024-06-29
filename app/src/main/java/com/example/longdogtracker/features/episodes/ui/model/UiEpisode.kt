package com.example.longdogtracker.features.episodes.ui.model

data class UiEpisode(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val season: Int,
    val hasKnownLongDog: Boolean,
    val foundLongDog: Boolean,
    val longDogLocation: String?
)
