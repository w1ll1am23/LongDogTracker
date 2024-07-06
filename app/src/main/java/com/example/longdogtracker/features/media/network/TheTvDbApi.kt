package com.example.longdogtracker.features.media.network

import com.example.longdogtracker.features.media.network.model.thetvdb.TheTvDbLoginBody
import com.example.longdogtracker.features.media.network.model.thetvdb.TheTvDbLoginResponse
import com.example.longdogtracker.features.media.network.model.thetvdb.TheTvDbMovie
import com.example.longdogtracker.features.media.network.model.thetvdb.TheTvDbSeason
import com.example.longdogtracker.features.media.network.model.thetvdb.TheTvDbSeries
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TheTvDbApi {

    @POST("login")
    fun getOauthToken(@Body loginBody: TheTvDbLoginBody): Call<TheTvDbLoginResponse>

    @GET("series/353546/extended")
    fun getSeries(): Call<TheTvDbSeries>

    @GET("seasons/{id}/extended")
    fun getSeason(@Path("id") id: Int): Call<TheTvDbSeason>

    @GET("movies/341113/extended")
    fun getMovie(): Call<TheTvDbMovie>
}