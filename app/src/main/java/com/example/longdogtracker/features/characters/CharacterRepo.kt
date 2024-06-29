package com.example.longdogtracker.features.characters

import android.util.Log
import com.example.longdogtracker.features.characters.model.UiCharacter
import com.example.longdogtracker.features.episodes.network.TheTvDbApi
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode
import com.example.longdogtracker.features.episodes.ui.model.UiSeason
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.settingOauthToken
import com.example.longdogtracker.room.CharacterDao
import com.example.longdogtracker.room.EpisodeDao
import com.example.longdogtracker.room.RoomCharacter
import com.example.longdogtracker.room.RoomEpisode
import com.example.longdogtracker.room.RoomSeason
import com.example.longdogtracker.room.SeasonDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterRepo @Inject constructor(
    private val theTvDbApi: TheTvDbApi,
    private val characterDao: CharacterDao,
    private val settingsPreferences: SettingsPreferences,
) {

    suspend fun getCharactersForSeries(): List<UiCharacter> {
        return withContext(Dispatchers.IO) {
            var characters = characterDao.getAll()
            var hadToFetchFromService = false
            // The DB is empty need to fetch from the service
            if (characters.isEmpty()) {
                settingsPreferences.readStringPreference(settingOauthToken)?.let { token ->
                    hadToFetchFromService = true
                    val result = theTvDbApi.getSeries(token).execute()
                    if (result.isSuccessful) {
                        result.body()?.data?.characters?.let { theTvDbCharacters ->
                            val charactersArray = theTvDbCharacters.map {
                                val names = it.name.split(" - ")
                                RoomCharacter(
                                    id = it.id,
                                    name = names[0],
                                    aka = names.getOrNull(1),
                                    image = it.image
                                )
                            }.toTypedArray()
                            characterDao.insertAll(*charactersArray)
                        }
                    } else {
                        Log.d("CharactersRepo", "Failed to fetch data from service. Response: ${result.code()}")
                    }
                } ?: run {
                    Log.d("CharactersRepo", "No oauth token in shared prefs")
                }
            }
            if (hadToFetchFromService) {
                characters = characterDao.getAll()
            }
            characters.map {
                UiCharacter(id = it.id, name = it.name, aka = it.aka, image = it.image)
            }
        }
    }
}