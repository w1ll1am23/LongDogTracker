package com.example.longdogtracker.features.episodes.network

import com.example.longdogtracker.features.episodes.network.model.TheTvDbLoginBody
import com.example.longdogtracker.features.episodes.network.model.TheTvDbLoginResponse
import com.example.longdogtracker.features.episodes.network.model.TheTvDbSeason
import com.example.longdogtracker.features.episodes.network.model.TheTvDbSeries
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface TheTvDbApi {

    @POST("login")
    fun getOauthToken(@Body loginBody: TheTvDbLoginBody): Call<TheTvDbLoginResponse>

    @GET("series/353546/extended")
    fun getSeries(): Call<TheTvDbSeries>

    @GET("seasons/{id}/extended")
    fun getSeason(@Path("id") id: Int): Call<TheTvDbSeason>
}