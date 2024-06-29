package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episodes")
data class RoomEpisode(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val number: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
)