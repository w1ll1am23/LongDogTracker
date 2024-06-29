package com.example.longdogtracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class RoomCharacter(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val aka: String?,
    val image: String?,
)