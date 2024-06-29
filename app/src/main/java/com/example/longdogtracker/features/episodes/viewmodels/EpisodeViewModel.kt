package com.example.longdogtracker.features.episodes.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.episodes.EpisodesRepo
import com.example.longdogtracker.features.episodes.model.EpisodesUIState
import com.example.longdogtracker.features.episodes.model.UiEpisode
import com.example.longdogtracker.features.episodes.model.UiSeason
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

    private var seasonEpisodeMap: Map<UiSeason, List<UiEpisode>> = mutableMapOf()

    fun loadInitialData() {
        viewModelScope.launch {
            episodesRepo.getSeasonsForSeries()?.let {
                if (it.isNotEmpty()) {
                    seasonEpisodeMap = episodesRepo.getEpisodes(it)
                } else {
                    Log.d("EpisodeViewModel", "Empty seasons returned from repo")

                }
            } ?: {
                Log.d("EpisodeViewModel", "Null seasons returned from repo")
            }
            episodesMutableStateFlow.value = EpisodesUIState.Episodes(seasonEpisodeMap)
        }
    }
}