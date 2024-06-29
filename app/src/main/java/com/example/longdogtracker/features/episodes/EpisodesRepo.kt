package com.example.longdogtracker.features.episodes

import android.util.Log
import com.example.longdogtracker.features.episodes.model.UiEpisode
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
import com.example.longdogtracker.room.EpisodeDao
import com.example.longdogtracker.room.RoomEpisode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EpisodesRepo @Inject constructor(
    val theTvDbApi: TheTvDbApi,
    private val episodeDao: EpisodeDao
) {
    val apiKey = ""
    val token = ""

    suspend fun getSeries() {
        withContext(Dispatchers.IO) {
            val result = theTvDbApi.getSeries(token).execute()
            if (result.isSuccessful) {
                result.body()?.data?.name?.let { Log.d("EpisodesRepo", it) }
            } else {
                Log.d("EpisodesRepo", "Failed")
            }
        }
    }

    suspend fun getEpisodes(): List<UiEpisode>? {
        return withContext(Dispatchers.IO) {
            val dbEpisodes = episodeDao.getAll()
            // The DB is empty so we need to fetch from the service
            if (dbEpisodes.isEmpty()) {
                Log.d("EpisodesRepo", "Calling service to get episodes")
                val result = theTvDbApi.getSeason(token).execute()
                if (result.isSuccessful) {
                    result.body()?.data?.episodes?.let { episodes ->
                        val episodesArray = episodes.map {
                            RoomEpisode(
                                id = it.id,
                                number = it.number,
                                title = it.name,
                                description = it.overview,
                                imageUrl = it.image,
                            )
                        }.toTypedArray()
                        episodeDao.insertAll(*episodesArray)
                        episodes.map { UiEpisode(it.name, it.overview, it.image) }
                    }

                } else {
                    Log.d("EpisodesRepo", "Failed")
                    emptyList()
                }
            } else {
                Log.d("EpisodesRepo", "Using episodes from DB")
                dbEpisodes.map { UiEpisode(it.title, it.description, it.imageUrl) }
            }

        }
    }
}