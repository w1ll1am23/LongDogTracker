package com.example.longdogtracker.features.characters.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.longdogtracker.features.characters.model.CharactersUIState
import com.example.longdogtracker.features.characters.viewmodels.CharactersViewModel

@Composable
fun CharactersScreen() {
    val viewModel = hiltViewModel<CharactersViewModel>()

    val viewState by viewModel.charactersStateFlow.collectAsState()

    HandleUiState(uiState = viewState)

    LaunchedEffect(key1 = null) {
        viewModel.getCharacters()
    }
}

@Composable
private fun HandleUiState(uiState: CharactersUIState) {
    when (uiState) {
        is CharactersUIState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is CharactersUIState.Characters -> {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(uiState.character) { character ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
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

        is CharactersUIState.Error -> {
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