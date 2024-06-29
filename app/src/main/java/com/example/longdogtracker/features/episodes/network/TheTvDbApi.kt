package com.example.longdogtracker.features.episodes.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface TheTvDbApi {

    @GET("series/353546/extended")
    fun getSeries(@Header("Authorization") authorization: String): Call<TheTvDbSeries>

    @GET("seasons/1735939/extended")
    fun getSeason(@Header("Authorization") authorization: String): Call<TheTvDbSeason>
}