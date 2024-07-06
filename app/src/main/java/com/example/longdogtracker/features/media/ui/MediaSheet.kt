package com.example.longdogtracker.features.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.R
import com.example.longdogtracker.features.media.ui.model.MediaType
import com.example.longdogtracker.features.media.ui.model.UiMedia
import com.example.longdogtracker.features.media.viewmodels.MediaSheetViewModel
import com.example.longdogtracker.ui.theme.BlueyBodyAccentDark
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight
import com.example.longdogtracker.ui.theme.BlueyBodySnout
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme

@Composable
fun EpisodeSheet(episode: UiMedia) {
    val viewModel = hiltViewModel<MediaSheetViewModel>()

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
    episode: UiMedia,
    updateLongDogStatus: (Int) -> Unit,
    updateLongDogLocation: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                Icon(
                    Icons.Default.Edit,
                    modifier = Modifier.size(16.dp),
                    contentDescription = ""
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {

            val color = when {
                episode.longDogsFound > 0 -> BlueyBodySnout
                episode.knownLongDogCount > 0 && episode.longDogsFound == 0 -> Color.LightGray
                else -> Color.Black
            }
            val longDogStatus = when {
                episode.longDogsFound > 0 -> "Found"
                episode.knownLongDogCount > 0 && episode.longDogsFound == 0 -> "Not Found"
                else -> "Unknown"
            }
            if (editMode) {
                LongDogStepper(count = episode.longDogsFound) { count ->
                    updateLongDogStatus(count)
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

@Composable
fun LongDogStepper(count: Int, updateQuantity: (Int) -> Unit) {
    val currentCount = remember {
        mutableIntStateOf(count)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            enabled = currentCount.intValue != 0,
            onClick = {
                currentCount.intValue -= 1
                updateQuantity(currentCount.intValue)
            },
            colors = IconButtonColors(
                containerColor = BlueyBodyAccentDark,
                contentColor = BlueyBodyAccentLight,
                disabledContentColor = Color.LightGray,
                disabledContainerColor = Color.Gray
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.remove),
                contentDescription = "",
                tint = BlueyBodyAccentLight
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val dogColor = when {
                currentCount.intValue == 0 -> {
                    Color.LightGray
                }

                else -> {
                    BlueyBodySnout
                }
            }
            Image(
                painter = painterResource(id = R.drawable.long_dog_black),
                colorFilter = ColorFilter.tint(dogColor),
                modifier = Modifier.size(32.dp),
                contentDescription = null,
            )
            Text(currentCount.intValue.toString())
        }

        IconButton(
            onClick = {
                currentCount.intValue += 1
                updateQuantity(currentCount.intValue)
            },
            colors = IconButtonColors(
                containerColor = BlueyBodyAccentDark,
                contentColor = BlueyBodyAccentLight,
                disabledContentColor = Color.LightGray,
                disabledContainerColor = Color.Gray
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "",
                tint = BlueyBodyAccentLight
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LongDogStepperPreview() {
    LongDogTrackerPrimaryTheme {
        Column {
            LongDogStepper(0, {})
            LongDogStepper(1, {})

        }
    }
}


@Preview(showBackground = true)
@Composable
fun LongDogSheetPreview() {
    LongDogTrackerPrimaryTheme {
        HandleUiState(
            UiMedia(
                id  = 1,
                apiId = "",
                title = "title",
                type = MediaType.Movie,
                description = "description",
                longDogLocation = null,
                longDogsFound = 1,
                knownLongDogCount = 1,
                imageUrl = null
            ), {}, {})
    }
}