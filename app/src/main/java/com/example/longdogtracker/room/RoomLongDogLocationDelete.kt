package com.example.longdogtracker.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class RoomEpisodeLongDogLocationDelete(
    @ColumnInfo(name = "longDogLocationId")
    val longDogLocationId: Int,
)