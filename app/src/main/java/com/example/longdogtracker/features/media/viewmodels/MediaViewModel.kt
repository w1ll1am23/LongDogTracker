package com.example.longdogtracker.features.media.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.media.ui.model.MediaListItem
import com.example.longdogtracker.features.media.ui.model.MediaUIState
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingFilterFound
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaViewModel @Inject constructor(
    private val episodesRepo: EpisodesRepo,
    private val settingsPreferences: SettingsPreferences,
    ) : ViewModel() {
    private val episodesMutableStateFlow =
        MutableStateFlow<MediaUIState>(MediaUIState.Loading)
    val episodesStateFlow: StateFlow<MediaUIState> = episodesMutableStateFlow

    fun loadInitialData() {
        Log.d("MediaViewModel", "Loading initial state VM")
        viewModelScope.launch {
            val hideFound = settingsPreferences.readBooleanPreference(settingFilterFound)
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
                                        if (episode.knownLongDogCount != episode.longDogsFound || !hideFound) {
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

    fun sheetDismissed() {
        loadInitialData()
    }
}