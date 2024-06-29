package com.example.longdogtracker.features.characters.viewmodels

import androidx.lifecycle.ViewModel
import com.example.longdogtracker.features.characters.model.CharactersUIState
import com.example.longdogtracker.features.settings.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    val settingsPerfs: SettingsPreferences,
) : ViewModel() {

    private val charactersMutableStateFlow =
        MutableStateFlow<CharactersUIState>(CharactersUIState.Loading)
    val charactersStateFlow: StateFlow<CharactersUIState> = charactersMutableStateFlow

}