package com.example.longdogtracker.features.settings.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.longdogtracker.features.media.EpisodesRepo
import com.example.longdogtracker.features.settings.SettingsPreferences
import com.example.longdogtracker.features.settings.model.ActionType
import com.example.longdogtracker.features.settings.model.ExportEpisode
import com.example.longdogtracker.features.settings.model.ExportSeason
import com.example.longdogtracker.features.settings.model.ExportType
import com.example.longdogtracker.features.settings.model.SettingType
import com.example.longdogtracker.features.settings.model.SettingsState
import com.example.longdogtracker.features.settings.model.UiSetting
import com.example.longdogtracker.features.settings.model.allSettingsList
import com.example.longdogtracker.room.LongDogDatabase
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPerfs: SettingsPreferences,
    private val longDogDatabase: LongDogDatabase,
    private val episodesRepo: EpisodesRepo,
) : ViewModel() {

    private var context: Context? = null

    private val settingsMutableStateFlow =
        MutableStateFlow<SettingsState>(SettingsState.Loading)
    val settingsStateFlow: StateFlow<SettingsState> = settingsMutableStateFlow

    fun resetContext() {
        context = null
    }

    fun loadSettings(context: Context) {
        this.context = context
        settingsMutableStateFlow.value = SettingsState.Loading
        viewModelScope.launch {
            val allUiSettings = mutableListOf<UiSetting>()
            allSettingsList.forEach { setting ->
                allUiSettings.add(
                    when (setting.type) {
                        is SettingType.OnOff -> {
                            UiSetting.ToggleSetting(
                                id = setting.id,
                                title = setting.title,
                                description = setting.description,
                                currentState = settingsPerfs.readBooleanPreference(setting.id),
                                updateSetting = ::updateToggleSetting
                            )
                        }

                        is SettingType.StringValue -> {
                            UiSetting.StringSetting(
                                id = setting.id,
                                title = setting.title,
                                description = setting.description,
                                value = settingsPerfs.readStringPreference(setting.id),
                                updateSetting = ::updateStringSetting
                            )
                        }

                        is SettingType.Action -> {
                            UiSetting.ActionSetting(
                                id = setting.id,
                                title = setting.title,
                                description = setting.description,
                                updateSetting = { handleSettingAction(setting.type.action) },
                                actionCopy = setting.type.actionCopy,
                            )
                        }
                    }
                )
            }
            settingsMutableStateFlow.value = SettingsState.Settings(allUiSettings)
        }
    }

    private fun updateToggleSetting(id: String, toggledOn: Boolean) {
        Log.i("SettingsViewModel", "Updating $id to $toggledOn")
        settingsPerfs.writeBooleanPreference(id, toggledOn)
    }

    private fun updateStringSetting(id: String, value: String?) {
        Log.i("SettingsViewModel", "Updating $id to $value")
        value?.let {
            settingsPerfs.writeStringPreference(id, it)
        }
    }

    private fun handleSettingAction(action: ActionType) {
        when (action) {
            ActionType.BACKUP -> {
                backupDb()
            }

            ActionType.RESET -> {
                resetCache()
            }
        }
    }

    private fun backupDb() {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val seasons = episodesRepo.getSeasonsForSeries(true)) {
                is EpisodesRepo.GetSeasonsResult.Failure -> {
                    // TODO: Show an error
                }

                is EpisodesRepo.GetSeasonsResult.Seasons -> {
                    when (val episodes = episodesRepo.getEpisodes(seasons.seasons)) {
                        is EpisodesRepo.GetEpisodesResult.Episodes -> {
                            val exportSeasons: MutableList<ExportSeason> = mutableListOf()
                            episodes.episodes.forEach { (season, episodes) ->
                                val exportEpisodes: MutableList<ExportEpisode> = mutableListOf()
                                episodes.forEach { episode ->
                                    exportEpisodes.add(
                                        ExportEpisode(
                                            episode.longDogLocations?.map { it.location }?.toList()
                                                ?: emptyList(),
                                            episode.episode.toInt()
                                        )
                                    )
                                }
                                exportSeasons.add(ExportSeason(season.number, exportEpisodes))
                            }
                            val moshi = Moshi.Builder().build()
                            val jsonAdapter: JsonAdapter<ExportType> =
                                moshi.adapter(ExportType::class.java)
                            val exportString = jsonAdapter.toJson(ExportType(exportSeasons))
                            val clipboardManager = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

                            val clipData = ClipData.newPlainText("backup", exportString)

                            clipboardManager.setPrimaryClip(clipData)
                        }

                        is EpisodesRepo.GetEpisodesResult.Failure -> {
                            // TODO: Show an error
                        }
                    }

                }
            }
        }
    }

    private fun resetCache() {
        viewModelScope.launch(context = Dispatchers.IO) {
            longDogDatabase.clearAllTables()
        }
    }


}