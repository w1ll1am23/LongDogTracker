package com.example.longdogtracker.features.episodes

import android.util.Log
import com.example.longdogtracker.BuildConfig
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
import com.example.longdogtracker.features.episodes.network.model.TheTvDbLoginBody
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode
import com.example.longdogtracker.features.episodes.ui.model.UiSeason
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingOauthToken
import com.example.longdogtracker.room.EpisodeDao
import com.example.longdogtracker.room.RoomEpisode
import com.example.longdogtracker.room.RoomSeason
import com.example.longdogtracker.room.SeasonDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EpisodesRepo @Inject constructor(
    private val theTvDbApi: TheTvDbApi,
    private val episodeDao: EpisodeDao,
    private val seasonDao: SeasonDao,
    private val settingsPreferences: SettingsPreferences,
) {

    private fun isNotLoggedIn() =
        settingsPreferences.readStringPreference(settingOauthToken) == null

    private suspend fun login() {
        val result = theTvDbApi.getOauthToken(TheTvDbLoginBody(BuildConfig.THE_TV_DB_API_KEY)).execute()
        if (result.isSuccessful) {
            result.body()?.data?.token?.let {
                settingsPreferences.writeStringPreference(settingOauthToken, it)
            } ?: {
                Log.e(
                    "EpisodesRepo",
                    "Success logging in but token wasn't in response"
                )
            }
        } else {
            Log.e(
                "EpisodesRepo",
                "Failed to log in. Response: ${result.code()}"
            )
        }
    }

    suspend fun getSeasonsForSeries(): List<UiSeason> {
        var seasonWereFetchedFromService = false
        return withContext(Dispatchers.IO) {
            var seasons = seasonDao.getAll()
            // The DB is empty need to fetch from the service
            if (seasons.isEmpty()) {
                if (isNotLoggedIn()) {
                    login()
                }
                seasonWereFetchedFromService = true
                val result = theTvDbApi.getSeries().execute()
                if (result.isSuccessful) {
                    // Insert in to DB
                    result.body()?.data?.seasons?.let { theTvDbSeasons ->
                        val seasonsArray = theTvDbSeasons.mapNotNull {
                            if (it.type.type == "official") {
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
                    }
                } else {
                    Log.d(
                        "EpisodesRepo",
                        "Failed to fetch data from service. Response: ${result.code()}"
                    )
                }

            }
            if (seasonWereFetchedFromService) {
                seasons = seasonDao.getAll()
            }
            seasons.map { UiSeason(id = it.id, number = it.number) }
        }
    }

    suspend fun getEpisodes(seasons: List<UiSeason>): Map<UiSeason, List<UiEpisode>> {
        return withContext(Dispatchers.IO) {
            val seasonEpisodeMap = mutableMapOf<UiSeason, List<UiEpisode>>()
            var hadToFetchSeason = false
            seasons.forEach { season ->
                val episodes = episodeDao.getAllBySeason(season.number)
                seasonEpisodeMap[season] = episodes.map {
                    UiEpisode(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        imageUrl = it.imageUrl,
                        season = it.season,
                        hasKnownLongDog = it.hasKnownLongDog,
                        foundLongDog = it.allLongDogsFound,
                        longDogLocation = it.longDogLocation,
                        episode = it.episode
                    )
                }.sortedBy { it.episode }
            }
            seasonEpisodeMap.forEach { (season, episodes) ->
                if (episodes.isEmpty()) {
                    if (!hadToFetchSeason) {
                        hadToFetchSeason = true
                    }
                    Log.d(
                        "EpisodesRepo",
                        "Calling service to get episodes for season ${season.number}"
                    )
                    if (isNotLoggedIn()) {
                        login()
                    }
                    val result = theTvDbApi.getSeason(season.id).execute()
                    if (result.isSuccessful) {
                        val longDogMap = getKnownLongDogsMap()
                        result.body()?.data?.episodes?.let { theTvDbEpisodes ->
                            val episodesArray = theTvDbEpisodes.map {
                                RoomEpisode(
                                    id = it.id,
                                    season = it.seasonNumber,
                                    episode = it.number,
                                    title = it.name,
                                    description = it.overview,
                                    imageUrl = it.image,
                                    hasKnownLongDog = longDogMap[it.seasonNumber]?.get(it.number) != null,
                                    allLongDogsFound = false,
                                    foundUnknownLongDog = false,
                                    longDogLocation = longDogMap[it.seasonNumber]?.get(it.number)
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
                    seasonEpisodeMap[season] = episodes.map {
                        UiEpisode(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            imageUrl = it.imageUrl,
                            season = it.season,
                            hasKnownLongDog = it.hasKnownLongDog,
                            foundLongDog = it.allLongDogsFound,
                            longDogLocation = it.longDogLocation,
                            episode = it.episode,
                        )
                    }.sortedBy { it.episode }
                }
            }
            seasonEpisodeMap
        }
    }

    suspend fun updateEpisode(uiEpisode: UiEpisode) {
        withContext(Dispatchers.IO) {
            val episode = episodeDao.getEpisodeById(uiEpisode.id)
            val updatedEpisode = episode.copy(
                allLongDogsFound = uiEpisode.foundLongDog,
                longDogLocation = uiEpisode.longDogLocation
            )
            episodeDao.updateEpisode(updatedEpisode)
        }
    }

    private fun getKnownLongDogsMap(): Map<Int, Map<Int, String>> {
        val knownLongDogs = KnownLongDogs()
        return mapOf(
            Pair(1, knownLongDogs.seasonOne),
            Pair(2, knownLongDogs.seasonTwo),
            Pair(3, knownLongDogs.seasonThree)
        )
    }
}