package com.example.longdogtracker.features.episodes.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.features.episodes.ui.model.EpisodeFilterSheetUIState
import com.example.longdogtracker.features.episodes.viewmodels.EpisodeFilterSheetViewModel

@Composable
fun EpisodeFilterSheet() {
    val viewModel = hiltViewModel<EpisodeFilterSheetViewModel>()

    val uiState by viewModel.episodeFilterStateFlow.collectAsState()

    HandleUiState(uiState, viewModel::filter)

    LaunchedEffect(key1 = null) {
        viewModel.getFilterValues()
    }


}

@Composable
private fun HandleUiState(uiState: EpisodeFilterSheetUIState, filter: (Int, Boolean) -> Unit) {
    Box(modifier = Modifier.padding(16.dp)) {
        when (uiState) {
            is EpisodeFilterSheetUIState.Filters -> {
                LazyColumn {
                    item { Text("Seasons") }
                    item {
                        LazyRow {
                            items(uiState.seasons) {
                                FilterChip(
                                    selected = it.selected,
                                    onClick = { filter.invoke(it.number, !it.selected) },
                                    label = { Text(text = it.number.toString()) })

                            }
                        }
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