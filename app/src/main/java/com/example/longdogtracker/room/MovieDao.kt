package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAll(): List<RoomMovie>

    @Query("SELECT * FROM movies WHERE id IS :id")
    fun getMovieById(id: Int): RoomMovie

    @Insert
    fun insertAll(vararg movie: RoomMovie)

    @Update
    fun updateMovie(movie: RoomMovie)

}