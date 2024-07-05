package com.example.longdogtracker.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomEpisode::class, RoomSeason::class, RoomCharacter::class, RoomMovie::class], version = 1)
abstract class LongDogDatabase : RoomDatabase() {
    abstract fun episodeDao(): EpisodeDao
    abstract fun seasonDao(): SeasonDao
    abstract fun characterDao(): CharacterDao
    abstract fun movieDao(): MovieDao
}