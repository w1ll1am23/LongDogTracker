package com.example.longdogtracker

import android.content.Context
import androidx.room.Room
import com.example.longdogtracker.features.media.network.TheTvDbApi
import com.example.longdogtracker.network.AddAuthInterceptor
import com.example.longdogtracker.network.TheTvDb
import com.example.longdogtracker.network.UnauthorizedInterceptor
import com.example.longdogtracker.room.LongDogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
    ).createFromAsset("known_longdogs.db").build()

    @Singleton
    @Provides
    fun provideEpisodeDao(db: LongDogDatabase) = db.episodeDao()

    @Singleton
    @Provides
    fun provideSeasonDao(db: LongDogDatabase) = db.seasonDao()

    @Singleton
    @Provides
    fun provideCharacterDao(db: LongDogDatabase) = db.characterDao()

    @Singleton
    @Provides
    fun provideLongDogLocationDao(db: LongDogDatabase) = db.longDogLocationDao()

    @Provides
    @TheTvDb
    fun providesTheTvDbOkHttp(
        authInterceptor: AddAuthInterceptor,
        unauthorizedInterceptor: UnauthorizedInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(unauthorizedInterceptor)
        .build()

    @Provides
    @TheTvDb
    fun provideTheTvDbRetroFit(@TheTvDb okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://api4.thetvdb.com/v4/")
        .build()

    @Provides
    fun provideTheTvDbApi(@TheTvDb retrofit: Retrofit): TheTvDbApi = retrofit.create(TheTvDbApi::class.java)
}