package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes")
    fun getAll(): List<RoomEpisode>

    @Insert
    fun insertAll(vararg users: RoomEpisode)

    @Update
    fun updateEpisode(episode: RoomEpisode)

}