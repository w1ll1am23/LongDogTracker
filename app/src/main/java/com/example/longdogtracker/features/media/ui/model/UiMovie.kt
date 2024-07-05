package com.example.longdogtracker.features.media.ui.model

data class UiMovie(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val knownLongDogCount: Int,
    val longDogsFound: Int,
    val longDogLocation: String?
)
