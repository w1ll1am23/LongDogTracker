package com.example.longdogtracker.features.characters.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.features.characters.viewmodels.CharactersViewModel

@Composable
fun CharactersScreen() {
    val viewModel = hiltViewModel<CharactersViewModel>()
    val viewState by viewModel.charactersStateFlow.collectAsState()
    LaunchedEffect(key1 = null) {

    }
}