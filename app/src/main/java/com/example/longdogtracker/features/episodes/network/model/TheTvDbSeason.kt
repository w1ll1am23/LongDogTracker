package com.example.longdogtracker.features.episodes.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TheTvDbSeason(val data: TheTvDbSeasonData)

@JsonClass(generateAdapter = true)
data class TheTvDbSeasonData(val episodes: List<TheTvDbEpisode>)

@JsonClass(generateAdapter = true)
data class TheTvDbEpisode(
    val id: Int,
    val name: String,
    val overview: String,
    val image: String?,
    val number: Int,
    val seasonNumber: Int,
)