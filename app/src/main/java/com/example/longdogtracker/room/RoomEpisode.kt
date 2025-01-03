package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episodes")
data class RoomEpisode(
    @PrimaryKey
    val id: Int,
    val apiId: String,
    val season: Int,
    val episode: Int,
    val seasonEpisode: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
)