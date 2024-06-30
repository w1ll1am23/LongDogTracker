package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.Update

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    fun getAll(): List<RoomCharacter>

    @Insert
    fun insertAll(vararg characters: RoomCharacter)

}