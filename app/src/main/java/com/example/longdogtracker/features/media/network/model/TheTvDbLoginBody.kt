package com.example.longdogtracker.features.media.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TheTvDbLoginBody(val apikey: String)
