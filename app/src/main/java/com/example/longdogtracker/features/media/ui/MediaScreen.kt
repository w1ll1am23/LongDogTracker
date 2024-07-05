package com.example.longdogtracker.features.media.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.longdogtracker.R
import com.example.longdogtracker.features.main.LongDogTopBar
import com.example.longdogtracker.features.main.TopBarNavigation
import com.example.longdogtracker.features.media.ui.model.MediaUIState
import com.example.longdogtracker.features.media.ui.model.UiEpisode
import com.example.longdogtracker.features.media.viewmodels.MediaViewModel
import com.example.longdogtracker.ui.theme.BingoBodyPrimary
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight
import com.example.longdogtracker.ui.theme.BlueyBodySnout
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
                        mutableStateOf<UiEpisode?>(null)
                    }
                    LazyColumn {
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
                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    shape = RoundedCornerShape(size = 16.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        val color = when {
                                            movie.longDogsFound > 0 -> BlueyBodySnout
                                            movie.knownLongDogCount > 0 && movie.longDogsFound == 0 -> Color.LightGray
                                            else -> Color.Black
                                        }
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                movie.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = TextUnit(16F, TextUnitType.Sp)
                                            )
                                            Image(
                                                painter = painterResource(id = R.drawable.long_dog_black),
                                                colorFilter = ColorFilter.tint(color),
                                                modifier = Modifier.size(32.dp),
                                                contentDescription = null,
                                            )
                                        }
                                        AsyncImage(
                                            modifier = Modifier
                                                .padding(vertical = 8.dp)
                                                .fillMaxWidth(),
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(movie.imageUrl)
                                                .memoryCachePolicy(CachePolicy.ENABLED)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .crossfade(true)
                                                .build(),
                                            contentScale = ContentScale.FillWidth,
                                            contentDescription = null,
                                        )
                                        Text(
                                            movie.description,
                                            fontSize = TextUnit(13F, TextUnitType.Sp)
                                        )
                                    }
                                }
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
                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            selectedEpisode.value = episode
                                            showEpisodeSheet.value = true
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    shape = RoundedCornerShape(size = 16.dp)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        val color = when {
                                            episode.longDogsFound > 0 -> BlueyBodySnout
                                            episode.knownLongDogCount > 0 && episode.longDogsFound == 0 -> Color.LightGray
                                            else -> Color.Black
                                        }
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                episode.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = TextUnit(16F, TextUnitType.Sp)
                                            )
                                            Image(
                                                painter = painterResource(id = R.drawable.long_dog_black),
                                                colorFilter = ColorFilter.tint(color),
                                                modifier = Modifier.size(32.dp),
                                                contentDescription = null,
                                            )
                                        }
                                        Text(
                                            "Season: ${episode.season} Episode: ${episode.episode}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = TextUnit(16F, TextUnitType.Sp)
                                        )
                                        AsyncImage(
                                            modifier = Modifier
                                                .padding(vertical = 8.dp)
                                                .fillMaxWidth(),
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(episode.imageUrl)
                                                .memoryCachePolicy(CachePolicy.ENABLED)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .crossfade(true)
                                                .build(),
                                            contentScale = ContentScale.FillWidth,
                                            contentDescription = null,
                                        )
                                        Text(
                                            episode.description,
                                            fontSize = TextUnit(13F, TextUnitType.Sp)
                                        )
                                    }
                                }
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