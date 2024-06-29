package com.example.longdogtracker.features.characters.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.longdogtracker.R
import com.example.longdogtracker.features.characters.model.CharactersUIState
import com.example.longdogtracker.features.characters.viewmodels.CharactersViewModel

@Composable
fun CharactersScreen() {
    val viewModel = hiltViewModel<CharactersViewModel>()

    val viewState by viewModel.charactersStateFlow.collectAsState()

    HandleUiState(state = viewState)

    LaunchedEffect(key1 = null) {
        viewModel.getCharacters()
    }
}

@Composable
private fun HandleUiState(state: CharactersUIState) {
    when (state) {
        is CharactersUIState.Characters -> {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(state.character) { character ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp),
                        elevation = 4.dp,
                        shape = RoundedCornerShape(size = 16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    character.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = TextUnit(16F, TextUnitType.Sp)
                                )
                            }
                            character.aka?.let {
                                Text(text = "AKA: $it")
                            }
                            AsyncImage(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(character.image)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .crossfade(true)
                                    .build(),
                                contentScale = ContentScale.FillWidth,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
        CharactersUIState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}