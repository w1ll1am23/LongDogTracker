package com.example.longdogtracker.features.media.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.R
import com.example.longdogtracker.features.main.LongDogTopBar
import com.example.longdogtracker.features.main.TopBarNavigation
import com.example.longdogtracker.features.media.ui.model.MediaUIState
import com.example.longdogtracker.features.media.ui.model.UiMedia
import com.example.longdogtracker.features.media.viewmodels.MediaViewModel
import com.example.longdogtracker.ui.theme.BingoBodyPrimary
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(navigate: () -> Unit) {
    val viewModel = hiltViewModel<MediaViewModel>()

    val uiState = viewModel.episodesStateFlow.collectAsState()

    val showFilterSheet = remember {
        mutableStateOf(false)
    }

    HandleUiState(uiState = uiState.value, viewModel::sheetDismissed, { topBarNavigation ->
        when (topBarNavigation) {
            TopBarNavigation.SETTINGS -> {
                navigate.invoke()
            }

            TopBarNavigation.FILTER -> {
                showFilterSheet.value = true
            }
        }
    }, viewModel::search)

    if (showFilterSheet.value) {
        ModalBottomSheet(
            containerColor = BlueyBodyAccentLight,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetState = rememberModalBottomSheetState(),
            windowInsets = WindowInsets.safeDrawing,
            dragHandle = { BottomSheetDefaults.HiddenShape },
            onDismissRequest = {
                showFilterSheet.value = false
                viewModel.sheetDismissed()
            }) {
            EpisodeFilterSheet()
        }
    }

    LaunchedEffect(key1 = null) {
        Log.d("MediaScreen", "Loading initial state from composable")
        viewModel.loadInitialData()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HandleUiState(
    uiState: MediaUIState,
    sheetDismissed: () -> Unit,
    topBarNavigate: (TopBarNavigation) -> Unit,
    search: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            LongDogTrackerPrimaryTheme {
                LongDogTopBar(topBarNavigate, search)
            }
        },
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            when (uiState) {
                MediaUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is MediaUIState.Media -> {
                    val showEpisodeSheet = remember {
                        mutableStateOf(false)
                    }
                    val selectedEpisode = remember {
                        mutableStateOf<UiMedia?>(null)
                    }
                    LazyColumn {
                        if (uiState.books.isNotEmpty()) {
                            stickyHeader {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BlueyBodyAccentLight)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BlueyBodyAccentLight)
                                    ) {
                                        Text(
                                            "Books",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                        Text(
                                            "${uiState.books.size}",
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                            items(uiState.books) { book ->
                                MediaCard(
                                    book
                                )
                            }
                        }
                        if (uiState.movies.isNotEmpty()) {
                            stickyHeader {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BlueyBodyAccentLight)
                                ) {
                                    Text(
                                        "Movies",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            items(uiState.movies) { movie ->
                                MediaCard(
                                    movie
                                )
                            }

                        }
                        uiState.seasonEpisodeMap.forEach { (season, episodes) ->
                            stickyHeader {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BlueyBodyAccentLight)
                                ) {
                                    when (season.number) {
                                        0 -> {
                                            Text("Specials", modifier = Modifier.padding(16.dp))
                                        }

                                        999 -> {
                                            Text("Results", modifier = Modifier.padding(16.dp))
                                        }

                                        else -> {
                                            Text(
                                                "Season: ${season.number}",
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${episodes.size} episodes",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }

                            items(episodes) { episode ->
                                MediaCard(
                                    episode
                                )
                            }
                        }
                    }
                    if (showEpisodeSheet.value) {
                        selectedEpisode.value?.let {
                            ModalBottomSheet(
                                containerColor = if (it.title.lowercase() == "bingo") BingoBodyPrimary else BlueyBodyAccentLight,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                sheetState = rememberModalBottomSheetState(),
                                windowInsets = WindowInsets.safeDrawing,
                                dragHandle = { BottomSheetDefaults.HiddenShape },
                                onDismissRequest = {
                                    showEpisodeSheet.value = false
                                    sheetDismissed.invoke()
                                }) {
                                EpisodeSheet(episode = it)
                            }
                        }
                    }
                }

                is MediaUIState.Error -> {
                    val showError = remember {
                        mutableStateOf(true)
                    }
                    if (showError.value) {
                        AlertDialog(
                            title = { Text(text = stringResource(id = R.string.error_unknown_title)) },
                            text = { Text(text = stringResource(id = uiState.errorMessage)) },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showError.value = false
                                    }
                                ) {
                                    Text(stringResource(id = R.string.error_dismiss_button_copy))
                                }
                            },
                            onDismissRequest = { showError.value = false },
                            confirmButton = { })
                    }
                }

            }
        }
    }


}