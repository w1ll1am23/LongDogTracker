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

    fun updateLongDogLocationFoundStatus(locationId: Int, found: Boolean) {
        viewModelScope.launch {
            episodesRepo.updateLocationFoundStat(locationId, found)
        }
    }
}