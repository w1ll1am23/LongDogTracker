package com.example.longdogtracker.features.media.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.R
import com.example.longdogtracker.features.media.ui.model.UiMedia
import com.example.longdogtracker.features.media.viewmodels.MediaSheetViewModel
import com.example.longdogtracker.ui.theme.BlueyBodyAccentDark
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight
import com.example.longdogtracker.ui.theme.BlueyBodySnout
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme
import kotlinx.coroutines.delay

@Composable
fun MediaSheet(uiMedia: UiMedia, dismissSheet: () -> Unit) {
    val viewModel = hiltViewModel<MediaSheetViewModel>()

    HandleUiState(
        uiMedia = uiMedia,
        dismissSheet = dismissSheet,
        updateLongDogStatus = viewModel::updateLongDogStatus,
        updateLongDogLocation = viewModel::updateLongDogLocation
    )

    LaunchedEffect(key1 = uiMedia) {
        viewModel.initEpisode(uiMedia)
    }


}

@Composable
private fun HandleUiState(
    uiMedia: UiMedia,
    dismissSheet: () -> Unit,
    updateLongDogStatus: (Int) -> Unit,
    updateLongDogLocation: (String) -> Unit
) {
    var locations by remember {
        mutableStateOf(uiMedia.longDogLocations)
    }
    Scaffold(
        floatingActionButton = {
            Button(onClick = {
                val newLocations = locations?.toMutableList() ?: mutableListOf()
                newLocations.add("")
                locations = newLocations
            }) {
                Text(text = "Add Long Dog")
            }
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(contentPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Long Dog Locations",
                    Modifier
                        .semantics { heading() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = dismissSheet) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
            }
            LazyColumn {
                locations?.let { locations ->
                    itemsIndexed(locations) { index, location ->
                        if (location.isNotEmpty()) {
                            LongDogLocationCard(
                                number = index + 1,
                                location = location,
                                found = false
                            )
                        } else {
                            NewLongDogLocationCard(updateLongDogLocation)
                        }
                    }
                }
            }
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

@Composable
fun NewLongDogLocationCard(updateLocation: (String) -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .requiredHeight(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(size = 16.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { textUpdate ->
                text = textUpdate
            },
            placeholder = { Text(text = "Enter the new location here") })
    }
    LaunchedEffect(key1 = text) {
        delay(500)
        updateLocation.invoke(text)
    }
}

@Composable
fun LongDogLocationCard(number: Int, location: String, found: Boolean) {
    var flip by remember { mutableStateOf(false) }
    var flipRotation by remember { mutableFloatStateOf(0f) }
    val animationSpec = tween<Float>(1000, easing = CubicBezierEasing(0.4f, 0.0f, 0.8f, 0.8f))
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .requiredHeight(250.dp)
            .graphicsLayer {
                rotationY = flipRotation
                cameraDistance = 64 * density
            }
            .clickable {
                flip = !flip
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(size = 16.dp)
    ) {
        if (flipRotation < 90f) {
            LongDogLocationCardFront(
                number, found
            )
        } else {
            LongDogLocationCardBack(location = location, found = found)
        }
    }

    LaunchedEffect(key1 = flip) {
        if (flip) {
            animate(
                initialValue = 0f,
                targetValue = 180f,
                animationSpec = animationSpec
            ) { value: Float, _: Float ->
                flipRotation = value
            }
        } else {
            animate(
                initialValue = flipRotation,
                targetValue = 0f,
                animationSpec = animationSpec
            ) { value: Float, _: Float ->
                flipRotation = value
            }
        }
    }
}

@Composable
fun LongDogLocationCardFront(number: Int, found: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(text = "Long dog #$number", modifier = Modifier.align(Alignment.TopStart))
        Text(text = "Tap to view location", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun LongDogLocationCardBack(location: String, found: Boolean) {
    var checked by remember {
        mutableStateOf(found)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .graphicsLayer {
                rotationY = 180f
            }
            .padding(16.dp),
    ) {
        Text(text = location, modifier = Modifier.weight(0.8f))
        Checkbox(checked = checked, onCheckedChange = { checked = !checked })
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