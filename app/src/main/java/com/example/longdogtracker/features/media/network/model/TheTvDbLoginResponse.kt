package com.example.longdogtracker.features.media.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TheTvDbLoginResponse(val data: TheTvDbLoginResponseData)

@JsonClass(generateAdapter = true)

data class TheTvDbLoginResponseData(val token: String)
