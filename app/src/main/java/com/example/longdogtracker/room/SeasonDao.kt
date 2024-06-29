package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons")
    fun getAll(): List<RoomSeason>

    @Insert
    fun insertAll(vararg seasons: RoomSeason)

}