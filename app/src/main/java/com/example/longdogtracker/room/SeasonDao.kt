package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons")
    fun getAll(): List<RoomSeason>

    @Query("SELECT * FROM seasons WHERE number IN (:seasons)")
    fun getSeasons(seasons: List<Int>): List<RoomSeason>

    @Insert
    fun insertAll(vararg seasons: RoomSeason)

}