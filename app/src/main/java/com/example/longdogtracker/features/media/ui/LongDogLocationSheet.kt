package com.example.longdogtracker.features.media.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.features.media.ui.model.UiLongDogLocation
import com.example.longdogtracker.features.media.ui.model.UiEpisode
import com.example.longdogtracker.features.media.viewmodels.MediaSheetViewModel

@Composable
fun LongDogLocationSheet(uiEpisode: UiEpisode, dismissSheet: () -> Unit) {
    val viewModel = hiltViewModel<MediaSheetViewModel>()

    HandleUiState(
        uiEpisode = uiEpisode,
        dismissSheet = dismissSheet,
        updateLongDogLocationFoundStatus = viewModel::updateLongDogLocationFoundStatus,
        addNewLongDogLocation = viewModel::addNewLongDogLocation
    )

    LaunchedEffect(key1 = uiEpisode) {
        viewModel.initEpisode(uiEpisode)
    }


}

@Composable
private fun HandleUiState(
    uiEpisode: UiEpisode,
    dismissSheet: () -> Unit,
    updateLongDogLocationFoundStatus: (Int, Boolean) -> Unit,
    addNewLongDogLocation: (UiEpisode, String) -> Unit,
) {
    var showNewLongDogLocation by remember {
        mutableStateOf(false)
    }
    Scaffold(
        floatingActionButton = {
            Button(onClick = { showNewLongDogLocation = true }) {
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
            if (!showNewLongDogLocation) {
                LazyColumn {
                    uiEpisode.longDogLocations?.let { locations ->
                        items(locations) { location ->
                            LongDogLocationCard(
                                uiLongDogLocation = location,
                                updateLongDogLocationFoundStatus = updateLongDogLocationFoundStatus
                            )
                        }
                    }
                }
            }
            if (showNewLongDogLocation) {
                NewLongDogLocationCard { location ->
                    addNewLongDogLocation(uiEpisode, location)
                }
            }
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
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = text,
                onValueChange = { textUpdate ->
                    text = textUpdate
                },
                placeholder = { Text(text = "Enter the new location here") })
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                Button(onClick = { updateLocation(text) }) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Composable
fun LongDogLocationCard(
    uiLongDogLocation: UiLongDogLocation,
    updateLongDogLocationFoundStatus: (Int, Boolean) -> Unit,
) {
    var flip by remember { mutableStateOf(uiLongDogLocation.found) }
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
                if (!uiLongDogLocation.found) {
                    flip = !flip
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(size = 16.dp)
    ) {
        if (flipRotation < 90f) {
            LongDogLocationCardFront(
                uiLongDogLocation
            )
        } else {
            LongDogLocationCardBack(uiLongDogLocation, updateLongDogLocationFoundStatus)
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
fun LongDogLocationCardFront(uiLongDogLocation: UiLongDogLocation) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = "Long dog #${uiLongDogLocation.number + 1}",
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(text = "Tap to view location", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun LongDogLocationCardBack(
    uiLongDogLocation: UiLongDogLocation, updateLongDogLocationFoundStatus: (Int, Boolean) -> Unit,
) {
    var checked by remember {
        mutableStateOf(uiLongDogLocation.found)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .graphicsLayer {
                rotationY = 180f
            }
            .padding(16.dp),
    ) {
        Text(text = uiLongDogLocation.location, modifier = Modifier.weight(0.8f))
        Checkbox(checked = checked, onCheckedChange = {
            checked = !checked
            updateLongDogLocationFoundStatus.invoke(uiLongDogLocation.id, checked)
        })
    }

}