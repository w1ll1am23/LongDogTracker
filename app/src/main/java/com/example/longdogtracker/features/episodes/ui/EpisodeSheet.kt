package com.example.longdogtracker.features.episodes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode

@Composable
fun EpisodeSheet(episode: UiEpisode) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Text(
                text = episode.title, fontWeight = FontWeight.Bold,
                fontSize = TextUnit(16F, TextUnitType.Sp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = episode.longDogLocation ?: "", fontSize = TextUnit(13F, TextUnitType.Sp))
    }
}