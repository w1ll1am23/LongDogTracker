package com.example.longdogtracker.features.episodes.network

import com.example.longdogtracker.features.episodes.network.model.TheTvDbSeason
import com.example.longdogtracker.features.episodes.network.model.TheTvDbSeries
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TheTvDbApi {

    @GET("series/353546/extended")
    fun getSeries(@Header("Authorization") authorization: String): Call<TheTvDbSeries>

    @GET("seasons/{id}/extended")
    fun getSeason(@Header("Authorization") authorization: String, @Path("id") id: Int): Call<TheTvDbSeason>
}