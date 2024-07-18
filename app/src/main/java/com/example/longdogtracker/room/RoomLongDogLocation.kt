package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "longdogs")
data class RoomEpisodeLongDogLocation(
    @PrimaryKey
    val longDogLocationId: Int,
    val seasonEpisode: String,
    val location: String,
    val found: Boolean,
    val userAdded: Boolean,
)