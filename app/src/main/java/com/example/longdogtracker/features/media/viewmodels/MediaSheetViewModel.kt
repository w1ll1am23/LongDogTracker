package com.example.longdogtracker.features.media.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.media.ui.model.UiMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaSheetViewModel @Inject constructor(private val episodesRepo: EpisodesRepo) :
    ViewModel() {

    private lateinit var uiEpisode: UiMedia

    fun initEpisode(uiEpisode: UiMedia) {
        this.uiEpisode = uiEpisode
    }

    fun updateLongDogStatus(found: Int) {
        updateEpisode(uiEpisode.copy(longDogsFound = found))
    }

    fun updateLongDogLocation(location: String) {
        val newLocations = uiEpisode.longDogLocations?.toMutableList() ?: mutableListOf()
        newLocations.add(location)
        updateEpisode(
            uiEpisode.copy(
                longDogLocations = newLocations
            )
        )
    }

    private fun updateEpisode(episode: UiMedia) {
        viewModelScope.launch {
            episodesRepo.updateEpisode(episode)
        }
    }
}