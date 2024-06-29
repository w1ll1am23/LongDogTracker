package com.example.longdogtracker.features.episodes.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TheTvDbSeries(val data: TheTvDbSeriesData)

@JsonClass(generateAdapter = true)
data class TheTvDbSeriesData(val characters: List<TheTvDbCharacter>, val seasons: List<TheTvDbSeriesSeason>)

@JsonClass(generateAdapter = true)
data class TheTvDbCharacter(
    val id: Int,
    val name: String,
    val image: String?,
)

@JsonClass(generateAdapter = true)
data class TheTvDbSeriesSeason(val id: Int, val number: Int, val type: TheTvDbSeasonType)

@JsonClass(generateAdapter = true)
data class TheTvDbSeasonType(val id: Int, val type: String)
