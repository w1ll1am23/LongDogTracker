package com.example.longdogtracker.features.media

import android.util.Log
import androidx.annotation.StringRes
import com.example.longdogtracker.R
import com.example.longdogtracker.features.media.network.TheTvDbApi
import com.example.longdogtracker.features.media.ui.model.UiLongDogLocation
import com.example.longdogtracker.features.media.ui.model.UiEpisode
import com.example.longdogtracker.features.media.ui.model.UiSeason
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingLastFetchEpisodesFromService
import com.example.longdogtracker.features.settings.model.settingLastFetchSeasonsFromService
import com.example.longdogtracker.features.settings.model.settingSeasonFilter
import com.example.longdogtracker.network.LoginServiceInteractor
import com.example.longdogtracker.room.EpisodeDao
import com.example.longdogtracker.room.LongDogLocationDao
import com.example.longdogtracker.room.RoomEpisode
import com.example.longdogtracker.room.RoomEpisodeLongDogLocation
import com.example.longdogtracker.room.RoomEpisodeLongDogLocationDelete
import com.example.longdogtracker.room.RoomEpisodeLongDogLocationFoundUpdate
import com.example.longdogtracker.room.RoomSeason
import com.example.longdogtracker.room.SeasonDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class EpisodesRepo @Inject constructor(
    private val theTvDbApi: TheTvDbApi,
    private val episodeDao: EpisodeDao,
    private val seasonDao: SeasonDao,
    private val longDogLocationDao: LongDogLocationDao,
    private val settingsPreferences: SettingsPreferences,
    private val loginServiceInteractor: LoginServiceInteractor,
) {

    suspend fun getSeasonsForSeries(ignoreFilters: Boolean = false): GetSeasonsResult {
        var seasonWereFetchedFromService = false
        return withContext(Dispatchers.IO) {
            val filterSeasons = settingsPreferences.readIntListPreference(
                settingSeasonFilter
            )
            var seasons = seasonDao.getAll()
            val lastServiceFetch =
                settingsPreferences.readLongPreference(settingLastFetchSeasonsFromService)
            val shouldRefresh = lastServiceFetch?.let {
                val then = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                val now = ZonedDateTime.now(
                    ZoneId.systemDefault()
                )
                Duration.between(then, now).toDays() > 1
            } ?: true
            // The DB is empty need to fetch from the service
            if (seasons.isEmpty() || shouldRefresh) {
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
                        val seasonsToInsert = theTvDbSeasons.mapNotNull { theTvDbSeason ->
                            if (theTvDbSeason.type.type == "official" && seasons.find { it.id == theTvDbSeason.id } == null) {
                                RoomSeason(
                                    id = theTvDbSeason.id,
                                    number = theTvDbSeason.number,
                                    type = theTvDbSeason.type.type
                                )
                            } else {
                                null
                            }
                        }
                        Log.d("EpisodesRepo", "Inserting Seasons")
                        if (seasonsToInsert.isNotEmpty()) {
                            seasonDao.insertAll(*seasonsToInsert.toTypedArray())
                        }
                        settingsPreferences.writeLongPreference(
                            settingLastFetchSeasonsFromService, ZonedDateTime.now(
                                ZoneId.systemDefault()
                            ).toEpochSecond()
                        )
                    }
                } else {
                    GetSeasonsResult.Failure(R.string.error_unknown_issue_fetching_seasons)
                }

            }
            if (seasonWereFetchedFromService) {
                seasons = seasonDao.getAll()
            }
            GetSeasonsResult.Seasons(
                seasons.mapNotNull {
                    if (filterSeasons == null || filterSeasons.contains(it.number) || ignoreFilters) {
                        UiSeason(id = it.id, number = it.number)
                    } else {
                        null
                    }
                })
        }
    }

    suspend fun getEpisodes(seasons: List<UiSeason>): GetEpisodesResult {
        return withContext(Dispatchers.IO) {
            val seasonEpisodeMap = mutableMapOf<UiSeason, List<UiEpisode>>()
            var hadToFetchFromService = false
            seasons.forEach { season ->
                val episodes = episodeDao.getAllBySeason(season.number)
                seasonEpisodeMap[season] = episodes.sortedBy { it.episode }.map {
                    UiEpisode(
                        id = it.id,
                        apiId = it.apiId,
                        title = it.title,
                        description = it.description,
                        imageUrl = it.imageUrl,
                        longDogsFound = it.longDogLocations?.count { location -> location.found }
                            ?: 0,
                        knownLongDogCount = it.longDogLocations?.count { location -> !location.userAdded }
                            ?: 0,
                        longDogLocations = it.longDogLocations?.mapIndexed { index, location ->
                            UiLongDogLocation(
                                location.longDogLocationId,
                                index,
                                location.location,
                                location.found
                            )
                        },
                        seasonEpisode = it.seasonEpisode,
                        season = it.season.toString(),
                        episode = it.episode.toString()
                    )
                }
            }
            val lastServiceFetch = settingsPreferences.readLongPreference(
                settingLastFetchEpisodesFromService
            )
            val shouldRefresh = lastServiceFetch?.let {
                val then = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                val now = ZonedDateTime.now(
                    ZoneId.systemDefault()
                )
                Duration.between(then, now).toDays() > 1
            } ?: true
            seasonEpisodeMap.forEach { (season, episodes) ->
                if (episodes.isEmpty() || shouldRefresh) {
                    if (!hadToFetchFromService) {
                        hadToFetchFromService = true
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
                        result.body()?.data?.episodes?.let { theTvDbEpisodes ->
                            val episodesArray = theTvDbEpisodes.mapNotNull { theTvDbEpisode ->
                                if (episodes.find { it.episode.toInt() == theTvDbEpisode.number } == null) {
                                    RoomEpisode(
                                        id = theTvDbEpisode.id,
                                        apiId = theTvDbEpisode.id.toString(),
                                        season = theTvDbEpisode.seasonNumber,
                                        episode = theTvDbEpisode.number,
                                        seasonEpisode = "${theTvDbEpisode.seasonNumber}${theTvDbEpisode.number}",
                                        title = theTvDbEpisode.name,
                                        description = theTvDbEpisode.overview,
                                        imageUrl = theTvDbEpisode.image,
                                    )
                                } else {
                                    null
                                }
                            }.toTypedArray()
                            episodeDao.insertAll(*episodesArray)
                        }
                    } else {
                        GetEpisodesResult.Failure(R.string.error_unknown_issue_fetching_episodes)
                    }
                }
            }
            // If we had to fetch from the service query the DB again to get the updates
            if (hadToFetchFromService) {
                settingsPreferences.writeLongPreference(
                    settingLastFetchEpisodesFromService, ZonedDateTime.now(
                        ZoneId.systemDefault()
                    ).toEpochSecond()
                )
                seasons.forEach { season ->
                    val episodes = episodeDao.getAllBySeason(season.number)
                    seasonEpisodeMap[season] = episodes.sortedBy { it.episode }.map {
                        UiEpisode(
                            id = it.id,
                            apiId = it.apiId,
                            title = it.title,
                            description = it.description,
                            imageUrl = it.imageUrl,
                            longDogsFound = it.longDogLocations?.count { location -> location.found }
                                ?: 0,
                            knownLongDogCount = it.longDogLocations?.count { location -> !location.userAdded }
                                ?: 0,
                            longDogLocations = it.longDogLocations?.mapIndexed { index, location ->
                                UiLongDogLocation(
                                    location.longDogLocationId,
                                    index,
                                    location.location,
                                    location.found
                                )
                            },
                            seasonEpisode = it.seasonEpisode,
                            season = it.season.toString(),
                            episode = it.episode.toString()
                        )
                    }
                }
            }
            GetEpisodesResult.Episodes(seasonEpisodeMap)
        }
    }

    suspend fun getEpisodesByQuery(query: String): GetEpisodesResult {
        return withContext(Dispatchers.IO) {
            val filterSeasons = settingsPreferences.readIntListPreference(
                settingSeasonFilter
            )
            val episodes =
                episodeDao.getAllEpisodeBySearch(query, filterSeasons ?: (0..10).toList())
            GetEpisodesResult.Episodes(
                mapOf(
                    Pair(
                        UiSeason(999, 999),
                        episodes.sortedBy { it.episode }.map {
                            UiEpisode(
                                id = it.id,
                                apiId = it.apiId,
                                title = it.title,
                                description = it.description,
                                imageUrl = it.imageUrl,
                                longDogsFound = it.longDogLocations?.count { location -> location.found }
                                    ?: 0,
                                knownLongDogCount = it.longDogLocations?.count { location -> !location.userAdded }
                                    ?: 0,
                                longDogLocations = it.longDogLocations?.mapIndexed { index, location ->
                                    UiLongDogLocation(
                                        location.longDogLocationId,
                                        index,
                                        location.location,
                                        location.found
                                    )
                                },
                                seasonEpisode = it.seasonEpisode,
                                season = it.season.toString(),
                                episode = it.episode.toString()
                            )
                        }
                    )
                )
            )
        }
    }

    suspend fun updateLocationFoundStat(
        id: Int,
        found: Boolean
    ) {
        withContext(Dispatchers.IO) {
            longDogLocationDao.updateLocation(RoomEpisodeLongDogLocationFoundUpdate(id, found))
        }
    }

    suspend fun deleteLocationFoundStat(
        id: Int,
    ) {
        withContext(Dispatchers.IO) {
            longDogLocationDao.deleteLocation(RoomEpisodeLongDogLocationDelete(id))
        }
    }

    suspend fun addNewLongDogLocation(
        uiEpisode: UiEpisode,
        location: String
    ) {
        withContext(Dispatchers.IO) {
            val locationId =
                uiEpisode.season.toInt() * 1000 + uiEpisode.episode.toInt() * 10 + (uiEpisode.longDogLocations?.let {
                    it.size + 1
                } ?: 0)
            longDogLocationDao.addNewLocation(
                RoomEpisodeLongDogLocation(
                    longDogLocationId = locationId,
                    seasonEpisode = uiEpisode.seasonEpisode,
                    location = location,
                    found = true,
                    userAdded = true
                )
            )
        }
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