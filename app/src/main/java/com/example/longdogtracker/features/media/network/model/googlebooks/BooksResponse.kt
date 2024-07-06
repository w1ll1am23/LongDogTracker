package com.example.longdogtracker.features.media.network.model.googlebooks

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BooksResponse(val items: List<Book>)

@JsonClass(generateAdapter = true)
data class Book(val id: String, val volumeInfo: VolumeInfo)

@JsonClass(generateAdapter = true)
data class VolumeInfo(val title: String, val description: String, val imageLinks: ImageLinks)

@JsonClass(generateAdapter = true)
data class ImageLinks(val thumbnail: String)
