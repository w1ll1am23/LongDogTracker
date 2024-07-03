package com.example.longdogtracker.features.episodes.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.episodes.EpisodesRepo
import com.example.longdogtracker.features.episodes.ui.model.EpisodesUIState
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode
import com.example.longdogtracker.features.episodes.ui.model.UiSeason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EpisodeViewModel @Inject constructor(private val episodesRepo: EpisodesRepo) : ViewModel() {
    private val episodesMutableStateFlow =
        MutableStateFlow<EpisodesUIState>(EpisodesUIState.Loading)
    val episodesStateFlow: StateFlow<EpisodesUIState> = episodesMutableStateFlow

    fun loadInitialData() {
        viewModelScope.launch {
            episodesMutableStateFlow.value =
                when (val seasonsResult = episodesRepo.getSeasonsForSeries()) {
                    is EpisodesRepo.GetSeasonsResult.Seasons -> {
                        when (val episodesResult =
                            episodesRepo.getEpisodes(seasonsResult.seasons)) {
                            is EpisodesRepo.GetEpisodesResult.Episodes -> {
                                EpisodesUIState.Episodes(episodesResult.episodes)
                            }

                            is EpisodesRepo.GetEpisodesResult.Failure -> {
                                EpisodesUIState.Error(episodesResult.errorMessage)
                            }
                        }
                    }

                    is EpisodesRepo.GetSeasonsResult.Failure -> EpisodesUIState.Error(seasonsResult.errorMessage)
                }
        }
    }

    fun sheetDismissed() {
        loadInitialData()
    }
}