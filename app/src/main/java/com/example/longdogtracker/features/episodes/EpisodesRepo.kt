package com.example.longdogtracker.features.episodes

import android.util.Log
import androidx.annotation.StringRes
import com.example.longdogtracker.BuildConfig
import com.example.longdogtracker.R
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
import com.example.longdogtracker.features.episodes.network.model.TheTvDbLoginBody
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode
import com.example.longdogtracker.features.episodes.ui.model.UiSeason
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingOauthToken
import com.example.longdogtracker.network.LoginServiceInteractor
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
    private val loginServiceInteractor: LoginServiceInteractor,
) {

    suspend fun getSeasonsForSeries(): GetSeasonsResult {
        var seasonWereFetchedFromService = false
        return withContext(Dispatchers.IO) {
            var seasons = seasonDao.getAll()
            // The DB is empty need to fetch from the service
            if (seasons.isEmpty()) {
                if (loginServiceInteractor.isNotLoggedIn()) {
                    when (val result = loginServiceInteractor.login()) {
                        LoginServiceInteractor.LoginStatus.Success -> Unit
                        is LoginServiceInteractor.LoginStatus.Error -> {
                            GetSeasonsResult.Failure(result.errorMessage)
                        }
                    }
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
                    GetSeasonsResult.Failure(R.string.error_unknown_issue_fetching_seasons)
                }

            }
            if (seasonWereFetchedFromService) {
                seasons = seasonDao.getAll()
            }
            GetSeasonsResult.Seasons(
                seasons.map { UiSeason(id = it.id, number = it.number) })
        }
    }

    suspend fun getEpisodes(seasons: List<UiSeason>): GetEpisodesResult {
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
                    if (loginServiceInteractor.isNotLoggedIn()) {
                        when (val result = loginServiceInteractor.login()) {
                            LoginServiceInteractor.LoginStatus.Success -> Unit
                            is LoginServiceInteractor.LoginStatus.Error -> {
                                GetEpisodesResult.Failure(result.errorMessage)
                            }
                        }
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
                    } else {
                        GetEpisodesResult.Failure(R.string.error_unknown_issue_fetching_episodes)
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
            GetEpisodesResult.Episodes(seasonEpisodeMap)
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

    sealed class GetSeasonsResult {
        data class Seasons(val seasons: List<UiSeason>) : GetSeasonsResult()
        data class Failure(@StringRes val errorMessage: Int) : GetSeasonsResult()
    }

    sealed class GetEpisodesResult {
        data class Episodes(val episodes: Map<UiSeason, List<UiEpisode>>) : GetEpisodesResult()
        data class Failure(@StringRes val errorMessage: Int) : GetEpisodesResult()
    }
}