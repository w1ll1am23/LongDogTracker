package com.example.longdogtracker.features.episodes

import android.util.Log
import com.example.longdogtracker.features.episodes.model.UiEpisode
import com.example.longdogtracker.features.episodes.model.UiSeason
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
import com.example.longdogtracker.room.EpisodeDao
import com.example.longdogtracker.room.RoomEpisode
import com.example.longdogtracker.room.RoomSeason
import com.example.longdogtracker.room.SeasonDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EpisodesRepo @Inject constructor(
    val theTvDbApi: TheTvDbApi,
    private val episodeDao: EpisodeDao,
    private val seasonDao: SeasonDao

) {
    val apiKey = ""
    val token = ""

    suspend fun getSeasonsForSeries(): List<UiSeason>? {
        return withContext(Dispatchers.IO) {
            val seasons = seasonDao.getAll()
            // The DB is empty need to fetch from the service
            if (seasons.isEmpty()) {
                val result = theTvDbApi.getSeries(token).execute()
                if (result.isSuccessful) {
                    result.body()?.data?.seasons?.let { theTvDbSeasons ->
                        val seasonsArray = theTvDbSeasons.mapNotNull {
                            if (it.type.type == "aired") {
                                RoomSeason(
                                    id = it.id,
                                    number = it.number,
                                    type = it.type.type
                                )
                            } else {
                                null
                            }
                        }.toTypedArray()
                        seasonDao.insertAll(*seasonsArray)
                        theTvDbSeasons.mapNotNull {
                            if (it.type.type == "official") {
                                UiSeason(it.id, it.number)
                            } else {
                                null
                            }
                        }
                    }
                } else {
                    Log.d("EpisodesRepo", "Failed")
                    emptyList()
                }
            } else {
                seasons.map { UiSeason(id = it.id, number = it.number) }
            }
        }
    }

    suspend fun getEpisodes(seasons: List<UiSeason>): Map<UiSeason, List<UiEpisode>> {
        return withContext(Dispatchers.IO) {
            val seasonEpisodeMap = mutableMapOf<UiSeason, List<UiEpisode>>()
            var hadToFetchSeason = false
            seasons.forEach { season ->
                val episodes = episodeDao.getAllBySeason(season.number)
                seasonEpisodeMap[season] = episodes.map { UiEpisode(
                    title = it.title,
                    description = it.description,
                    imageUrl = it.imageUrl,
                    season = it.season
                ) }
            }
            seasonEpisodeMap.forEach { (season, episodes) ->
                if (episodes.isEmpty()) {
                    if (!hadToFetchSeason) {
                        hadToFetchSeason = true
                    }
                    Log.d("EpisodesRepo", "Calling service to get episodes for season ${season.number}")
                    val result = theTvDbApi.getSeason(token, season.id).execute()
                    if (result.isSuccessful) {
                        result.body()?.data?.episodes?.let { theTvDbEpisodes ->
                            val episodesArray = theTvDbEpisodes.map {
                                RoomEpisode(
                                    id = it.id,
                                    season = it.seasonNumber,
                                    episode = it.number,
                                    title = it.name,
                                    description = it.overview,
                                    imageUrl = it.image,
                                )
                            }.toTypedArray()
                            episodeDao.insertAll(*episodesArray)
                        }
                    }
                }
            }
            // If we had to fetch from the service query the DB again to get the updates
            if (hadToFetchSeason) {
                seasons.forEach { season ->
                    val episodes = episodeDao.getAllBySeason(season.number)
                    seasonEpisodeMap[season] = episodes.map { UiEpisode(
                        title = it.title,
                        description = it.description,
                        imageUrl = it.imageUrl,
                        season = it.season
                    ) }
                }
            }
            seasonEpisodeMap
        }
    }
}