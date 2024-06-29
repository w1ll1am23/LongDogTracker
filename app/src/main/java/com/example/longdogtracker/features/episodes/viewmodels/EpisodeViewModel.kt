package com.example.longdogtracker.features.episodes.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.episodes.EpisodesRepo
import com.example.longdogtracker.features.episodes.model.EpisodesUIState
import com.example.longdogtracker.features.episodes.model.UiEpisode
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
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

    private var episodes: List<UiEpisode> = emptyList()

    fun getSeries() {
        viewModelScope.launch {
            episodes = episodesRepo.getEpisodes() ?: emptyList()
            episodesMutableStateFlow.value = EpisodesUIState.Episodes(episodes)
        }
    }
}