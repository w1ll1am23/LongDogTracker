package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes")
    fun getAll(): List<RoomEpisode>

    @Query("SELECT * FROM episodes WHERE id IS :id")
    fun getEpisodeById(id: Int): RoomEpisode

    @Query("SELECT * FROM episodes WHERE season IS :season")
    fun getAllBySeason(season: Int): List<RoomEpisode>

    @Query("SELECT * FROM episodes WHERE (title LIKE '%' || :query|| '%' OR description LIKE '%' || :query|| '%') and season IN (:seasons)")
    fun getAllEpisodeBySearch(query: String, seasons: List<Int>): List<RoomEpisode>

    @Insert
    fun insertAll(vararg episodes: RoomEpisode)

    @Update
    fun updateEpisode(episode: RoomEpisode)

}