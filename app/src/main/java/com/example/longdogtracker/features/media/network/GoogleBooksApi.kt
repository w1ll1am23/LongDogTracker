package com.example.longdogtracker.features.media.network

import com.example.longdogtracker.features.media.network.model.googlebooks.BooksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("books/v1/volumes?q=inauthor:\"Bluey\"&printType=books&maxResults=40")
    fun getBooks(@Query("key") key: String): Call<BooksResponse>
}