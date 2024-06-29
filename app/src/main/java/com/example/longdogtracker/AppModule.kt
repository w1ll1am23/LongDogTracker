package com.example.longdogtracker

import android.content.Context
import androidx.room.Room
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
import com.example.longdogtracker.room.LongDogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideLongDogDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        LongDogDatabase::class.java,
        "long_dogs"
    ).build()

    @Singleton
    @Provides
    fun provideEpisodeDao(db: LongDogDatabase) = db.episodeDao()

    @Singleton
    @Provides
    fun provideSeasonDao(db: LongDogDatabase) = db.seasonDao()

    @Singleton
    @Provides
    fun provideCharacterDao(db: LongDogDatabase) = db.characterDao()

    @Provides
    fun provideRetroFit() = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://api4.thetvdb.com/v4/")
        .build()

    @Provides
    fun provideTheTvDbApi(retrofit: Retrofit): TheTvDbApi = retrofit.create(TheTvDbApi::class.java)
}