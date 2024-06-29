package com.example.longdogtracker.features.episodes.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TheTvDbSeries(val data: TheTvDbSeriesData)

@JsonClass(generateAdapter = true)
data class TheTvDbSeriesData(val name: String)