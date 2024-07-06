package com.example.longdogtracker.features.media.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.media.ui.model.EpisodeFilterSheetUIState
import com.example.longdogtracker.features.media.ui.model.Season
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingBooksFilter
import com.example.longdogtracker.features.settings.model.settingMovieFilter
import com.example.longdogtracker.features.settings.model.settingSeasonFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MediaFilterSheetViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences,
    private val episodesRepo: EpisodesRepo
) : ViewModel() {

    private val episodeFilterMutableStateFlow =
        MutableStateFlow<EpisodeFilterSheetUIState>(EpisodeFilterSheetUIState.Loading)
    val episodeFilterStateFlow: StateFlow<EpisodeFilterSheetUIState> = episodeFilterMutableStateFlow

    private var seasons: List<Season> = emptyList()
    private var showMovies: Boolean = false
    private var showBooks: Boolean = false

    fun filterSeason(seasonNumber: Int, selected: Boolean) {
        val newSeasons: MutableList<Season> = mutableListOf()
        seasons.forEach {
            if (it.number == seasonNumber) {
                newSeasons.add(it.copy(selected = selected))
            } else {
                newSeasons.add(it)
            }
        }
        seasons = newSeasons

        settingsPreferences.writeIntListPreference(
            settingSeasonFilter,
            seasons.filter { it.selected }.map { it.number })

        episodeFilterMutableStateFlow.value =
            EpisodeFilterSheetUIState.Filters(seasons, showMovies, showBooks)
    }

    fun showMovies(filter: Boolean) {
        settingsPreferences.writeBooleanPreference(settingMovieFilter, filter)
        showMovies = filter
        episodeFilterMutableStateFlow.value =
            EpisodeFilterSheetUIState.Filters(seasons, showMovies, showBooks)
    }

    fun showBooks(filter: Boolean) {
        settingsPreferences.writeBooleanPreference(settingBooksFilter, filter)
        showBooks = filter
        episodeFilterMutableStateFlow.value =
            EpisodeFilterSheetUIState.Filters(seasons, showMovies, showBooks)
    }

    fun getFilterValues() {
        viewModelScope.launch {
            showMovies = settingsPreferences.readBooleanPreference(settingMovieFilter)
            showBooks = settingsPreferences.readBooleanPreference(settingBooksFilter)
            val seasonNumbers = when (val result = episodesRepo.getSeasonsForSeries(ignoreFilters = true)) {
                is EpisodesRepo.GetSeasonsResult.Seasons -> {
                    result.seasons.map { it.number }.toList()
                }

                is EpisodesRepo.GetSeasonsResult.Failure -> {
                    // TODO: Show some error
                    0..10
                }
            }
            val seasonFilter =
                settingsPreferences.readIntListPreference(settingSeasonFilter) ?: seasonNumbers
            seasons = seasonNumbers.map { Season(it, seasonFilter.contains(it)) }
            episodeFilterMutableStateFlow.value =
                EpisodeFilterSheetUIState.Filters(seasons, showMovies, showBooks)
        }

    }

}