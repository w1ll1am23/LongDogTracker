package com.example.longdogtracker.room

import androidx.room.Relation

data class RoomEpisodeWithLocations(
    val id: Int,
    val apiId: String,
    val season: Int,
    val episode: Int,
    val seasonEpisode: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    @Relation(parentColumn = "seasonEpisode", entityColumn = "seasonEpisode")
    val longDogLocations: List<RoomEpisodeLongDogLocation>?
)