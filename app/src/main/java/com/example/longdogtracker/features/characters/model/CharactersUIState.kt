package com.example.longdogtracker.features.characters.model

import androidx.annotation.StringRes

sealed class CharactersUIState {
    data object Loading : CharactersUIState()
    data class Characters(
        val character: List<UiCharacter>,
    ) : CharactersUIState()
    data class Error(@StringRes val errorMessage: Int) : CharactersUIState()
}