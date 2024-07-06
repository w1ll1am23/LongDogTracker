package com.example.longdogtracker.features.media.network.model.thetvdb

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TheTvDbMovie(val data: TheTvDbMovieData)

@JsonClass(generateAdapter = true)
data class TheTvDbMovieData(
    val id: Int,
    val image: String,
    val name: String,
    val description: String?
)
