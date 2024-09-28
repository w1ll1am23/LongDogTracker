package com.example.longdogtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LongDogLocationDao {
    @Query("SELECT * FROM longdogs")
    fun getAll(): List<RoomEpisodeLongDogLocation>

    @Update(entity = RoomEpisodeLongDogLocation::class)
    fun updateLocation(location: RoomEpisodeLongDogLocationFoundUpdate)

    @Insert(entity = RoomEpisodeLongDogLocation::class)
    fun addNewLocation(location: RoomEpisodeLongDogLocation)

}