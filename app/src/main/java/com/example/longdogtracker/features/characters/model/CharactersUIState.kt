package com.example.longdogtracker.features.characters.model

sealed class CharactersUIState {
    data object Loading : CharactersUIState()
    data class Characters(
        val name: String,
    ) : CharactersUIState()
}