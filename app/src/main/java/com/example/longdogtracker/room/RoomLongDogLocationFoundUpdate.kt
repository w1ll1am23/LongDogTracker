package com.example.longdogtracker.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class RoomEpisodeLongDogLocationFoundUpdate(
    @ColumnInfo(name = "longDogLocationId")
    val longDogLocationId: Int,
    @ColumnInfo(name = "found")
    val found: Boolean,
)