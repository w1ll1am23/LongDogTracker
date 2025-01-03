package com.example.longdogtracker.features.media.ui

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
import com.example.longdogtracker.features.media.ui.model.UiEpisode
import com.example.longdogtracker.features.media.viewmodels.MediaViewModel
import com.example.longdogtracker.features.search.ui.SearchSheet
import com.example.longdogtracker.ui.theme.BingoBodyPrimary
import com.example.longdogtracker.ui.theme.BlueyBodyAccentLight
import com.example.longdogtracker.ui.theme.BlueyBodyPrimary
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

    val showSearchSheet = remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    HandleUiState(
        uiState = uiState.value,
        listState = listState,
        viewModel::sheetDismissed
    ) { topBarNavigation ->
        when (topBarNavigation) {
            TopBarNavigation.SEARCH -> {
                showSearchSheet.value = true
            }

            TopBarNavigation.SETTINGS -> {
                navigate.invoke()
            }

            TopBarNavigation.FILTER -> {
                showFilterSheet.value = true
            }
        }
    }

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

    if (showSearchSheet.value) {
        ModalBottomSheet(
            containerColor = BlueyBodyPrimary,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            windowInsets = WindowInsets.safeDrawing,
            onDismissRequest = {
                showSearchSheet.value = false
            }) {
            SearchSheet { selected ->
                showSearchSheet.value = false
                when (val state = uiState.value) {
                    is MediaUIState.Media -> {
                        state.episodeLocations[selected.seasonEpisode]?.let {
                            coroutineScope.launch {
                                listState.scrollToItem(it)
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    LaunchedEffect(key1 = null) {
        viewModel.loadInitialData()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HandleUiState(
    uiState: MediaUIState,
    listState: LazyListState,
    sheetDismissed: () -> Unit,
    topBarNavigate: (TopBarNavigation) -> Unit,
) {
    Scaffold(
        topBar = {
            LongDogTrackerPrimaryTheme {
                LongDogTopBar(topBarNavigate)
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
                        mutableStateOf<UiEpisode?>(null)
                    }



                    LazyColumn(state = listState) {
                        uiState.items.forEach { listItem ->
                            when (listItem) {
                                is MediaListItem.Header -> {
                                    stickyHeader {
                                        MediaListHeader(
                                            listState = listState,
                                            header = listItem,
                                        )
                                    }
                                }

                                is MediaListItem.Media -> {
                                    item {
                                        MediaCard(
                                            uiEpisode = listItem.media
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
                                LongDogLocationSheet(uiEpisode = it) {
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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(BlueyBodyAccentLight)
            ) {
                IconButton(onClick = {
                    coroutineScope.launch {
                        listState.scrollToItem(
                            header.firstEpisodeIndex
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
                            header.lastEpisodeIndex
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