package com.example.longdogtracker.features.settings.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExportType(val seasons: List<ExportSeason>)

@JsonClass(generateAdapter = true)
data class ExportSeason(val seasonNumber: Int, val episodes: List<ExportEpisode>)

@JsonClass(generateAdapter = true)
data class ExportEpisode(
    val longDogs: List<String>,
    val episodeNumber: Int,
)
