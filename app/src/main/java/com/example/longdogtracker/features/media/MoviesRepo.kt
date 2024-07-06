package com.example.longdogtracker.features.media

import androidx.annotation.StringRes
import com.example.longdogtracker.R
import com.example.longdogtracker.features.media.network.TheTvDbApi
import com.example.longdogtracker.features.media.ui.model.MediaType
import com.example.longdogtracker.features.media.ui.model.UiMedia
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingLastFetchMoviesFromService
import com.example.longdogtracker.features.settings.model.settingMovieFilter
import com.example.longdogtracker.network.LoginServiceInteractor
import com.example.longdogtracker.room.MovieDao
import com.example.longdogtracker.room.RoomMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class MoviesRepo @Inject constructor(
    private val theTvDbApi: TheTvDbApi,
    private val movieDao: MovieDao,
    private val settingsPreferences: SettingsPreferences,
    private val loginServiceInteractor: LoginServiceInteractor,
) {

    suspend fun getAllMovies(ignoreFilters: Boolean = false): GetMoviesResult {
        var fetchedFromService = false
        return withContext(Dispatchers.IO) {
            val showMovies = settingsPreferences.readBooleanPreference(
                settingMovieFilter
            )
            var movies = movieDao.getAll()

            val lastServiceFetch =
                settingsPreferences.readLongPreference(settingLastFetchMoviesFromService)
            val shouldRefresh = lastServiceFetch?.let {
                val then = ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                val now = ZonedDateTime.now(
                    ZoneId.systemDefault()
                )
                Duration.between(then, now).toDays() > 1
            } ?: true
            // The DB is empty need to fetch from the service
            if (movies.isEmpty() || shouldRefresh) {
                if (loginServiceInteractor.isNotLoggedIn()) {
                    when (val result = loginServiceInteractor.login()) {
                        LoginServiceInteractor.LoginStatus.Success -> Unit
                        is LoginServiceInteractor.LoginStatus.Error -> {
                            GetMoviesResult.Failure(result.errorMessage)
                        }
                    }
                }

                fetchedFromService = true
                val result = theTvDbApi.getMovie().execute()
                if (result.isSuccessful) {
                    // Insert in to DB
                    result.body()?.data?.let { theTvDbMovie ->
                        if (movies.find { it.apiId == theTvDbMovie.id.toString() } == null) {
                            val moviesToInsert = listOf(
                                RoomMovie(
                                    id = 0,
                                    apiId = theTvDbMovie.id.toString(),
                                    title = theTvDbMovie.name,
                                    description = theTvDbMovie.description ?: "",
                                    imageUrl = theTvDbMovie.image,
                                    knownLongDogCount = 0,
                                    longDogsFound = 0,
                                    longDogLocation = null
                                )
                            ).toTypedArray()
                            movieDao.insertAll(*moviesToInsert)
                        }
                        settingsPreferences.writeLongPreference(
                            settingLastFetchMoviesFromService, ZonedDateTime.now(
                                ZoneId.systemDefault()
                            ).toEpochSecond()
                        )
                    }
                } else {
                    GetMoviesResult.Failure(R.string.error_unknown_issue_fetching_movies)
                }

            }
            if (fetchedFromService) {
                movies = movieDao.getAll()
            }
            GetMoviesResult.Movies(
                movies.mapNotNull {
                    if (showMovies || ignoreFilters) {
                        UiMedia(
                            id = it.id,
                            apiId = it.apiId,
                            type = MediaType.Movie,
                            title = it.title,
                            description = it.description,
                            imageUrl = it.imageUrl,
                            knownLongDogCount = it.knownLongDogCount,
                            longDogsFound = it.longDogsFound,
                            longDogLocation = it.longDogLocation
                        )
                    } else {
                        null
                    }
                })
        }
    }

    suspend fun updateMovie(uiMovie: UiMedia) {
        withContext(Dispatchers.IO) {
            val movie = movieDao.getMovieById(uiMovie.id)
            val updatedMovie = movie.copy(
                longDogsFound = uiMovie.longDogsFound,
                longDogLocation = uiMovie.longDogLocation
            )
            movieDao.updateMovie(updatedMovie)
        }
    }

    sealed class GetMoviesResult {
        data class Movies(val movies: List<UiMedia>) : GetMoviesResult()
        data class Failure(@StringRes val errorMessage: Int) : GetMoviesResult()
    }
}