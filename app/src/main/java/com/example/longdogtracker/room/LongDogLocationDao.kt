package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LongDogLocationDao {
    @Query("SELECT * FROM longdogs")
    fun getAll(): List<RoomLongDogLocation>

    @Query("SELECT * FROM longdogs WHERE season IS :season AND episode is :episode")
    fun getLocationByEpisode(season: Int, episode: Int): RoomLongDogLocation?

    @Query("SELECT * FROM longdogs WHERE season IS :season")
    fun getAllBySeason(season: Int): List<RoomLongDogLocation>

}