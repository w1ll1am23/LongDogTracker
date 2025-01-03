package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seasons")
data class RoomSeason(
    @PrimaryKey
    val id: Int,
    val number: Int,
    val type: String,
)