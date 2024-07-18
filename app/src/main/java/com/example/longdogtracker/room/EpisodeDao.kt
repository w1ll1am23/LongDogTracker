package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface EpisodeDao {
    @Transaction
    @Query("SELECT * FROM episodes")
    fun getAll(): List<RoomEpisodeWithLocations>

    @Transaction
    @Query("SELECT * FROM episodes WHERE id IS :id")
    fun getEpisodeById(id: Int): RoomEpisodeWithLocations

    @Transaction
    @Query("SELECT * FROM episodes WHERE season IS :season")
    fun getAllBySeason(season: Int): List<RoomEpisodeWithLocations>

    @Transaction
    @Query("SELECT * FROM episodes WHERE (title LIKE '%' || :query|| '%' OR description LIKE '%' || :query|| '%') and season IN (:seasons)")
    fun getAllEpisodeBySearch(query: String, seasons: List<Int>): List<RoomEpisodeWithLocations>

    @Insert
    fun insertAll(vararg episodes: RoomEpisode)

    @Update
    fun updateEpisode(episode: RoomEpisode)

}