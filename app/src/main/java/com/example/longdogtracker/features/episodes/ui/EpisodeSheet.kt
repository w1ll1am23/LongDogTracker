package com.example.longdogtracker.features.episodes.ui

import android.graphics.drawable.shapes.RoundRectShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.R
import com.example.longdogtracker.features.episodes.ui.model.UiEpisode
import com.example.longdogtracker.features.episodes.viewmodels.EpisodeSheetViewModel
import com.example.longdogtracker.features.episodes.viewmodels.EpisodeViewModel
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight

@Composable
fun EpisodeSheet(episode: UiEpisode) {
    val viewModel = hiltViewModel<EpisodeSheetViewModel>()

    HandleUiState(
        episode = episode,
        updateLongDogStatus = viewModel::updateLongDogStatus,
        updateLongDogLocation = viewModel::updateLongDogLocation
    )

    LaunchedEffect(key1 = episode) {
        viewModel.initEpisode(episode)
    }


}

@Composable
private fun HandleUiState(
    episode: UiEpisode,
    updateLongDogStatus: (Boolean) -> Unit,
    updateLongDogLocation: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        var editMode by remember {
            mutableStateOf(false)
        }
        var text by remember { mutableStateOf(episode.longDogLocation ?: "") }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Long Dog Status",
                Modifier.semantics { heading() },
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { editMode = true }) {
                Icon(Icons.Default.Edit, modifier = Modifier.size(16.dp), contentDescription = "")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {

            val color = when {
                episode.foundLongDog -> Color.Green
                episode.hasKnownLongDog -> Color.LightGray
                else -> Color.Black
            }
            val longDogStatus = when {
                episode.foundLongDog -> "Found"
                episode.hasKnownLongDog -> "Not Found"
                else -> "Unknown"
            }
            if (editMode) {
                IconButton(onClick = { updateLongDogStatus(false) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.long_dog_black),
                        contentDescription = "",
                        tint = Color.LightGray
                    )
                }
                IconButton(onClick = { updateLongDogStatus(true) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.long_dog_black),
                        contentDescription = "",
                        tint = Color.Green
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.long_dog_black),
                    colorFilter = ColorFilter.tint(color),
                    modifier = Modifier.size(32.dp),
                    contentDescription = null,
                )
                Text(text = " = $longDogStatus")
            }
        }


        Text(
            text = "Long Dog Location",
            Modifier.semantics { heading() },
            fontWeight = FontWeight.Bold
        )
        if (editMode) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    updateLongDogLocation(it)
                },
            )
        } else {
            Text(text = text)
        }
    }
}