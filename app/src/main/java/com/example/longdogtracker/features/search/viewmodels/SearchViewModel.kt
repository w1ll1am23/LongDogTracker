package com.example.longdogtracker.features.search.viewmodels

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.R
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.media.ui.model.MediaListItem
import com.example.longdogtracker.features.media.ui.model.UiEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val episodesRepo: EpisodesRepo,
) : ViewModel() {

    private val searchMutableStateFlow =
        MutableStateFlow<SearchUIState>(SearchUIState.Empty(R.string.search_emtpy_state_message))
    val searchStateFlow: StateFlow<SearchUIState> = searchMutableStateFlow

    fun searchLoaded() {
        searchMutableStateFlow.value = SearchUIState.Empty(R.string.search_emtpy_state_message)
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                searchMutableStateFlow.value = SearchUIState.Empty(R.string.search_emtpy_state_message)
            } else {
                when (val result = episodesRepo.getEpisodesByQuery(query)) {
                    is EpisodesRepo.GetEpisodesResult.Episodes -> {
                        val episodeResults = mutableListOf<UiEpisode>()
                        result.episodes.forEach { (_, episodes) ->
                            episodeResults.addAll(episodes)
                        }
                        searchMutableStateFlow.value =
                            SearchUIState.Media(episodeResults.mapIndexed { index, media ->
                                MediaListItem.Media(
                                    media.id,
                                    index,
                                    media
                                )
                            })
                    }

                    is EpisodesRepo.GetEpisodesResult.Failure -> {
                        // No results
                        Log.d("MediaViewModel", "No Results")
                    }
                }
            }
        }
    }

    sealed class SearchUIState {
        data class Empty(@StringRes val emptySearchMessage: Int) : SearchUIState()
        data object Loading : SearchUIState()
        data class Media(
            val items: List<MediaListItem.Media>,
        ) : SearchUIState()

        data class Error(@StringRes val errorMessage: Int) : SearchUIState()
    }
}