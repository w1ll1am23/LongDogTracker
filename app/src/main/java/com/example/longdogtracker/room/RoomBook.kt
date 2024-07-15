package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class RoomBook(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val apiId: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val knownLongDogCount: Int,
    val longDogsFound: Int,
    val longDogLocations: String?
)