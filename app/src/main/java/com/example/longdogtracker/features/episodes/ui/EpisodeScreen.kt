package com.example.longdogtracker.features.episodes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.longdogtracker.R
import com.example.longdogtracker.features.episodes.model.EpisodesUIState
import com.example.longdogtracker.features.episodes.viewmodels.EpisodeViewModel
import kotlin.random.Random

@Composable
fun EpisodesScreen() {
    val viewModel = hiltViewModel<EpisodeViewModel>()

    val uiState = viewModel.episodesStateFlow.collectAsState()

    HandleUiState(uiState = uiState.value)

    LaunchedEffect(key1 = null) {
        viewModel.getSeries()
    }
}

@Composable
private fun HandleUiState(uiState: EpisodesUIState) {
    when (uiState) {
        is EpisodesUIState.Episodes -> {
            LazyColumn {
                items(uiState.episodes) { episode ->
                    Card(modifier = Modifier.padding(8.dp), elevation = 4.dp, shape = RoundedCornerShape(size = 16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            val colors = remember {
                                listOf(ColorFilter.tint(Color.Gray), ColorFilter.tint(Color.Green), ColorFilter.tint(Color.Yellow))
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(episode.title, fontWeight = FontWeight.Bold, fontSize = TextUnit(16F, TextUnitType.Sp))
                                Image(
                                    painter = painterResource(id = R.drawable.long_dog_black),
                                    colorFilter = colors[Random.nextInt(0, 3)],
                                    modifier = Modifier.size(32.dp),
                                    contentDescription = null,
                                )
                            }
                            AsyncImage(
                                modifier = Modifier.padding(vertical = 8.dp),
                                model = episode.imageUrl ,
                                contentDescription = null,
                            )
                            Text(episode.description, fontSize = TextUnit(13F, TextUnitType.Sp))
                        }
                    }
                }
            }
        }
        EpisodesUIState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}