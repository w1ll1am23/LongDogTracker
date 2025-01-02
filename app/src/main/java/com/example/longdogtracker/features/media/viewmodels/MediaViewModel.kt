package com.example.longdogtracker.features.media.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.media.ui.model.MediaListItem
import com.example.longdogtracker.features.media.ui.model.MediaUIState
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingFilterFound
import com.example.longdogtracker.features.settings.model.settingFilterUnknown
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
            // TODO: Move this to the repo
            val hideFound = settingsPreferences.readBooleanPreference(settingFilterFound)
            val hideUnknown = settingsPreferences.readBooleanPreference(settingFilterUnknown)
            episodesMutableStateFlow.value =
                when (val seasonsResult = episodesRepo.getSeasonsForSeries()) {
                    is EpisodesRepo.GetSeasonsResult.Seasons -> {
                        when (val episodesResult =
                            episodesRepo.getEpisodes(seasonsResult.seasons)) {
                            is EpisodesRepo.GetEpisodesResult.Episodes -> {
                                val mediaList = mutableListOf<MediaListItem>()
                                val seasons = mutableListOf<Int>()
                                val headerLocations = mutableListOf<Int>()
                                val episodeLocations = mutableMapOf<String, Int>()
                                var count = 0
                                episodesResult.episodes.forEach { (season, episodes) ->
                                    headerLocations.add(count)
                                    seasons.add(season.number)
                                    count += 1
                                    val episodesToAdd = mutableListOf<MediaListItem.Media>()
                                    episodes.forEach { episode ->
                                        if (episode.knownLongDogCount == 0 && hideUnknown) {
                                            return@forEach
                                        }
                                        if ((episode.longDogsFound == episode.knownLongDogCount && episode.knownLongDogCount != 0) && hideFound) {
                                            return@forEach
                                        }
                                        episodeLocations[episode.seasonEpisode] = count
                                        episodesToAdd.add(
                                            MediaListItem.Media(
                                                index = count,
                                                key = episode.id,
                                                media = episode
                                            )
                                        )
                                        count += 1
                                    }
                                    mediaList.add(
                                        MediaListItem.Header(
                                            key = season.id,
                                            index = count,
                                            season = "Season: ${season.number}",
                                            longDogs = "${episodes.sumOf { it.longDogsFound }} of ${episodes.sumOf { it.knownLongDogCount }}",
                                            totalCount = "${episodes.size} episodes",
                                            firstEpisodeIndex = count - episodesToAdd.size,
                                            lastEpisodeIndex = count - 1
                                        )
                                    )
                                    mediaList.addAll(episodesToAdd)
                                }
                                MediaUIState.Media(mediaList, episodeLocations)
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