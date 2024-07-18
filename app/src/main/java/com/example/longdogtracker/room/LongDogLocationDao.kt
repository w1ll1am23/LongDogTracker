package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface LongDogLocationDao {
    @Query("SELECT * FROM longdogs")
    fun getAll(): List<RoomEpisodeLongDogLocation>

    @Update(entity = RoomEpisodeLongDogLocation::class)
    fun updateLocation(location: RoomEpisodeLongDogLocationFoundUpdate)

//    @Query("SELECT * FROM longdogs WHERE season IS :season AND episode is :episode")
//    fun getLocationsByEpisode(season: Int, episode: Int): List<RoomEpisodeLongDogLocation>?
//
//    @Query("SELECT * FROM longdogs WHERE season IS :season")
//    fun getAllBySeason(season: Int): List<RoomEpisodeLongDogLocation>

}