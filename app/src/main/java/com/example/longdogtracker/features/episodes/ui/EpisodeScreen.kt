package com.example.longdogtracker.features.episodes.ui

import androidx.compose.foundation.Image
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
import com.example.longdogtracker.features.episodes.ui.model.EpisodesUIState
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode
import com.example.longdogtracker.features.episodes.viewmodels.EpisodeViewModel
import com.example.longdogtracker.ui.theme.BingoBodyPrimary
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight

@Composable
fun EpisodesScreen() {
    val viewModel = hiltViewModel<EpisodeViewModel>()

    val uiState = viewModel.episodesStateFlow.collectAsState()

    HandleUiState(uiState = uiState.value)

    LaunchedEffect(key1 = null) {
        viewModel.loadInitialData()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HandleUiState(uiState: EpisodesUIState) {
    when (uiState) {
        EpisodesUIState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is EpisodesUIState.Episodes -> {
            val showEpisodeSheet = remember {
                mutableStateOf(false)
            }
            val selectedEpisode = remember {
                mutableStateOf<UiEpisode?>(null)
            }
            LazyColumn {
                uiState.seasonEpisodeMap.forEach { (season, episodes) ->
                    if (season.number != 0) {
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
                                        episode.foundLongDog -> Color.Green
                                        episode.hasKnownLongDog -> Color.LightGray
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
            }
            if (showEpisodeSheet.value) {
                selectedEpisode.value?.let {
                    ModalBottomSheet(
                        containerColor = if (it.title.lowercase() == "bingo") BingoBodyPrimary else BlueyBodyAccentLight,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        sheetState = rememberModalBottomSheetState(),
                        windowInsets = WindowInsets.safeDrawing,
                        dragHandle = { BottomSheetDefaults.HiddenShape },
                        onDismissRequest = { showEpisodeSheet.value = false }) {
                        EpisodeSheet(episode = it)
                    }
                }
            }
        }

        is EpisodesUIState.Error -> {
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