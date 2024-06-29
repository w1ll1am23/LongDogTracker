package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episodes")
data class RoomEpisode(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val season: Int,
    val episode: Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val hasKnownLongDog: Boolean,
    val allLongDogsFound: Boolean,
    val foundUnknownLongDog: Boolean,
    val longDogLocation: String?
)