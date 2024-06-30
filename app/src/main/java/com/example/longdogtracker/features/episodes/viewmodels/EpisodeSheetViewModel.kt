package com.example.longdogtracker.features.episodes.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.episodes.EpisodesRepo
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EpisodeSheetViewModel @Inject constructor(private val episodesRepo: EpisodesRepo) : ViewModel() {

    private lateinit var uiEpisode: UiEpisode

    fun initEpisode(uiEpisode: UiEpisode) {
        this.uiEpisode = uiEpisode
    }

    fun updateLongDogStatus(found: Boolean) {
        updateEpisode(uiEpisode.copy(foundLongDog = found))
    }

    fun updateLongDogLocation(location: String) {
        updateEpisode(uiEpisode.copy(longDogLocation = location))
    }

    private fun updateEpisode(episode: UiEpisode) {
        viewModelScope.launch {
            episodesRepo.updateEpisode(episode)
        }
    }
}