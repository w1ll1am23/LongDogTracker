package com.example.longdogtracker.features.media.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.features.media.ui.model.EpisodeFilterSheetUIState
import com.example.longdogtracker.features.media.viewmodels.MediaFilterSheetViewModel

@Composable
fun EpisodeFilterSheet() {
    val viewModel = hiltViewModel<MediaFilterSheetViewModel>()

    val uiState by viewModel.episodeFilterStateFlow.collectAsState()

    HandleUiState(uiState, viewModel::filterSeason, viewModel::showHideFound, viewModel::showHideUnknown)

    LaunchedEffect(key1 = null) {
        viewModel.getFilterValues()
    }
}

@Composable
private fun HandleUiState(
    uiState: EpisodeFilterSheetUIState,
    filterSeason: (Int, Boolean) -> Unit,
    showHideFound: (Boolean) -> Unit,
    showHideUnknown: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.padding(16.dp)) {
        when (uiState) {
            is EpisodeFilterSheetUIState.Filters -> {
                LazyColumn {
                    item { Text("Seasons:", Modifier.semantics { heading() }) }
                    item {
                        LazyRow {
                            items(uiState.seasons) {
                                FilterChip(
                                    selected = it.selected,
                                    onClick = { filterSeason.invoke(it.number, !it.selected) },
                                    label = { Text(text = it.number.toString()) })
                            }
                        }
                    }
                    item { Text("Hide Found Episodes:", Modifier.semantics { heading() }) }
                    item {
                        Switch(checked = uiState.hideFound , onCheckedChange = showHideFound)
                    }
                    item { Text("Hide Unknown Long Dog Episodes:", Modifier.semantics { heading() }) }
                    item {
                        Switch(checked = uiState.hideUnknown , onCheckedChange = showHideUnknown)
                    }
                }
            }

            EpisodeFilterSheetUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}