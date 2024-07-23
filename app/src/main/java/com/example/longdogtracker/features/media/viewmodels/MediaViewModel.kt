package com.example.longdogtracker.features.media.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.media.BooksRepo
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.media.MoviesRepo
import com.example.longdogtracker.features.media.ui.model.MediaListItem
import com.example.longdogtracker.features.media.ui.model.MediaUIState
import com.example.longdogtracker.features.media.ui.model.UiMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaViewModel @Inject constructor(
    private val episodesRepo: EpisodesRepo,
    private val moviesRepo: MoviesRepo,
    private val booksRepo: BooksRepo,
) : ViewModel() {
    private val episodesMutableStateFlow =
        MutableStateFlow<MediaUIState>(MediaUIState.Loading)
    val episodesStateFlow: StateFlow<MediaUIState> = episodesMutableStateFlow

    fun loadInitialData() {
        Log.d("MediaViewModel", "Loading initial state VM")
        viewModelScope.launch {
            var movies: List<UiMedia> = listOf()
            when (val moviesResult = moviesRepo.getAllMovies()) {
                is MoviesRepo.GetMoviesResult.Movies -> {
                    movies = moviesResult.movies
                }

                is MoviesRepo.GetMoviesResult.Failure -> {
                    MediaUIState.Error(moviesResult.errorMessage)
                }

            }
            var books: List<UiMedia> = listOf()
            when (val bookResult = booksRepo.getAllBooks()) {
                is BooksRepo.GetBooksResult.Books -> {
                    books = bookResult.books
                }

                is BooksRepo.GetBooksResult.Failure -> {
                    MediaUIState.Error(bookResult.errorMessage)
                }

            }
            episodesMutableStateFlow.value =
                when (val seasonsResult = episodesRepo.getSeasonsForSeries()) {
                    is EpisodesRepo.GetSeasonsResult.Seasons -> {
                        when (val episodesResult =
                            episodesRepo.getEpisodes(seasonsResult.seasons)) {
                            is EpisodesRepo.GetEpisodesResult.Episodes -> {
                                val mediaList = mutableListOf<MediaListItem>()
                                val seasons = mutableListOf<Int>()
                                val headerLocations = mutableListOf<Int>()
                                var count = 0
                                episodesResult.episodes.forEach { (season, episodes) ->
                                    headerLocations.add(count)
                                    seasons.add(season.number)
                                    mediaList.add(
                                        MediaListItem.Header(
                                            key = season.id,
                                            index = count,
                                            season = "Season: ${season.number}",
                                            longDogs = "${episodes.sumOf { it.longDogsFound }} of ${episodes.sumOf { it.knownLongDogCount }}",
                                            totalCount = "${episodes.size} episodes"
                                        )
                                    )
                                    count += 1
                                    episodes.forEach { episode ->
                                        mediaList.add(
                                            MediaListItem.Media(
                                                index = count,
                                                key = episode.id,
                                                media = episode
                                            )
                                        )
                                        count += 1
                                    }
                                }
                                MediaUIState.Media(mediaList, headerLocations)
                            }

                            is EpisodesRepo.GetEpisodesResult.Failure -> {
                                MediaUIState.Error(episodesResult.errorMessage)
                            }
                        }
                    }

                    is EpisodesRepo.GetSeasonsResult.Failure -> MediaUIState.Error(seasonsResult.errorMessage)
                }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadInitialData()
            } else {
                when (val result = episodesRepo.getEpisodesByQuery(query)) {
                    is EpisodesRepo.GetEpisodesResult.Episodes -> {
                        //episodesMutableStateFlow.value = MediaUIState.Media(result.episodes, emptyList(), emptyList())
                    }

                    is EpisodesRepo.GetEpisodesResult.Failure -> {
                        // No results
                        Log.d("MediaViewModel", "No Results")
                    }
                }
            }
        }
    }

    fun sheetDismissed() {
        loadInitialData()
    }
}