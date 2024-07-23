package com.example.longdogtracker.features.media.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.R
import com.example.longdogtracker.features.main.LongDogTopBar
import com.example.longdogtracker.features.main.TopBarNavigation
import com.example.longdogtracker.features.media.ui.model.MediaListItem
import com.example.longdogtracker.features.media.ui.model.MediaUIState
import com.example.longdogtracker.features.media.ui.model.UiMedia
import com.example.longdogtracker.features.media.viewmodels.MediaViewModel
import com.example.longdogtracker.ui.theme.BingoBodyPrimary
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(navigate: () -> Unit) {
    val viewModel = hiltViewModel<MediaViewModel>()

    val uiState = viewModel.episodesStateFlow.collectAsState()

    val showFilterSheet = remember {
        mutableStateOf(false)
    }

    HandleUiState(uiState = uiState.value, viewModel::sheetDismissed, { topBarNavigation ->
        when (topBarNavigation) {
            TopBarNavigation.SETTINGS -> {
                navigate.invoke()
            }

            TopBarNavigation.FILTER -> {
                showFilterSheet.value = true
            }
        }
    }, viewModel::search)

    if (showFilterSheet.value) {
        ModalBottomSheet(
            containerColor = BlueyBodyAccentLight,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetState = rememberModalBottomSheetState(),
            windowInsets = WindowInsets.safeDrawing,
            dragHandle = { BottomSheetDefaults.HiddenShape },
            onDismissRequest = {
                showFilterSheet.value = false
                viewModel.sheetDismissed()
            }) {
            EpisodeFilterSheet()
        }
    }

    LaunchedEffect(key1 = null) {
        Log.d("MediaScreen", "Loading initial state from composable")
        viewModel.loadInitialData()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HandleUiState(
    uiState: MediaUIState,
    sheetDismissed: () -> Unit,
    topBarNavigate: (TopBarNavigation) -> Unit,
    search: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            LongDogTrackerPrimaryTheme {
                LongDogTopBar(topBarNavigate, search)
            }
        },
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            when (uiState) {
                MediaUIState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is MediaUIState.Media -> {
                    val showMediaSheet = remember {
                        mutableStateOf(false)
                    }
                    val selectedMedia = remember {
                        mutableStateOf<UiMedia?>(null)
                    }
                    val listState = rememberLazyListState()


                    LazyColumn(state = listState) {
                        uiState.items.forEach { listItem ->
                            when (listItem) {
                                is MediaListItem.Header -> {
                                    stickyHeader {
                                        MediaListHeader(
                                            listState = listState,
                                            header = listItem,
                                            headerLocations = uiState.headerLocations
                                        )
                                    }
                                }

                                is MediaListItem.Media -> {
                                    item {
                                        MediaCard(
                                            uiMedia = listItem.media
                                        ) {
                                            selectedMedia.value = listItem.media
                                            showMediaSheet.value = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (showMediaSheet.value) {
                        selectedMedia.value?.let {
                            ModalBottomSheet(
                                containerColor = if (it.title.lowercase() == "bingo") BingoBodyPrimary else BlueyBodyAccentLight,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                                windowInsets = WindowInsets.safeDrawing,
                                dragHandle = { BottomSheetDefaults.HiddenShape },
                                onDismissRequest = {
                                    showMediaSheet.value = false
                                    sheetDismissed.invoke()
                                }) {
                                MediaSheet(uiMedia = it) {
                                    showMediaSheet.value = false
                                    sheetDismissed.invoke()
                                }
                            }
                        }
                    }
                }

                is MediaUIState.Error -> {
                    val showError = remember {
                        mutableStateOf(true)
                    }
                    if (showError.value) {
                        AlertDialog(
                            title = { Text(text = stringResource(id = R.string.error_unknown_title)) },
                            text = { Text(text = stringResource(id = uiState.errorMessage)) },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showError.value = false
                                    }
                                ) {
                                    Text(stringResource(id = R.string.error_dismiss_button_copy))
                                }
                            },
                            onDismissRequest = { showError.value = false },
                            confirmButton = { })
                    }
                }

            }
        }
    }

}

@Composable
private fun MediaListHeader(
    listState: LazyListState,
    header: MediaListItem.Header,
    headerLocations: List<Int>
) {
    val coroutineScope = rememberCoroutineScope()
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueyBodyAccentLight)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    header.season,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = header.totalCount,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = header.longDogs)
                Image(
                    painter = painterResource(id = R.drawable.long_dog_black),
                    modifier = Modifier.size(32.dp),
                    contentDescription = null,
                )
            }
            val currentHeaderLocation =
                headerLocations.indexOf(header.index)
            when {
                currentHeaderLocation == 0 && headerLocations.size > 1 -> {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            listState.scrollToItem(
                                headerLocations[1]
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }

                currentHeaderLocation == headerLocations.size - 1 && headerLocations.size > 1 -> {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            listState.scrollToItem(
                                headerLocations[currentHeaderLocation - 1]
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null
                        )
                    }
                }

                headerLocations.size > 2 -> {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(BlueyBodyAccentLight)
                    ) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(
                                    headerLocations[currentHeaderLocation - 1]
                                )
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(
                                    headerLocations[currentHeaderLocation + 1]
                                )
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}