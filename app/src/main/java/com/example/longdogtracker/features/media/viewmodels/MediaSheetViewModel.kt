package com.example.longdogtracker.features.media.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.media.ui.model.UiEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaSheetViewModel @Inject constructor(private val episodesRepo: EpisodesRepo) : ViewModel() {

    private lateinit var uiEpisode: UiEpisode

    fun initEpisode(uiEpisode: UiEpisode) {
        this.uiEpisode = uiEpisode
    }

    fun updateLongDogStatus(found: Int) {
        updateEpisode(uiEpisode.copy(longDogsFound = found))
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