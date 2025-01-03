package com.example.longdogtracker.features.characters.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.R
import com.example.longdogtracker.features.characters.CharacterRepo
import com.example.longdogtracker.features.characters.model.CharactersUIState
import com.example.longdogtracker.features.settings.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val characterRepo: CharacterRepo
) : ViewModel() {

    private val charactersMutableStateFlow =
        MutableStateFlow<CharactersUIState>(CharactersUIState.Loading)
    val charactersStateFlow: StateFlow<CharactersUIState> = charactersMutableStateFlow

    fun getCharacters() {
        viewModelScope.launch {
            charactersMutableStateFlow.value =
                when (val result = characterRepo.getCharactersForSeries()) {
                    is CharacterRepo.GetCharacterStatus.Characters -> CharactersUIState.Characters(
                        result.characters
                    )

                    is CharacterRepo.GetCharacterStatus.Failure -> CharactersUIState.Error(result.errorMessage)
                }
        }
    }
}