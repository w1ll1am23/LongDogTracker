package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "longdogs")
data class RoomLongDogLocation(
    @PrimaryKey
    val id: Int,
    val season: Int,
    val episode: Int,
    val locations: String,
)