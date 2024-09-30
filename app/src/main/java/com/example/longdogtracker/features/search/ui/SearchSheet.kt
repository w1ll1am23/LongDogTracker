package com.example.longdogtracker.features.search.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.longdogtracker.features.media.ui.MediaCard
import com.example.longdogtracker.features.search.viewmodels.SearchViewModel
import com.example.longdogtracker.ui.theme.LongDogTrackerPrimaryTheme
import kotlinx.coroutines.delay

@Composable
fun SearchSheet() {
    val viewModel = hiltViewModel<SearchViewModel>()

    val state = viewModel.searchStateFlow.collectAsState()

    HandleSearchState(state.value, viewModel::search)

    LaunchedEffect(key1 = null) {
        viewModel.searchLoaded()
    }

}

@Composable
private fun HandleSearchState(state: SearchViewModel.SearchUIState, search: (String) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        var query by remember {
            mutableStateOf<String?>(null)
        }

        OutlinedTextField(
            value = query ?: "",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    query = ""
                    search.invoke(query ?: "")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                }
            },
            onValueChange = { newQuery: String ->
                query = newQuery
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(64.dp),
            singleLine = true
        )
        LaunchedEffect(key1 = query) {
            query?.let {
                delay(500)
                search.invoke(it)
            }
        }
        when (state) {
            is SearchViewModel.SearchUIState.Error -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = state.errorMessage))
                }
            }

            SearchViewModel.SearchUIState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is SearchViewModel.SearchUIState.Media -> {
                LazyColumn {
                    state.items.forEach {
                        item {
                            MediaCard(
                                uiEpisode = it.media
                            ) {}
                        }
                    }
                }
            }

            is SearchViewModel.SearchUIState.Empty -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = state.emptySearchMessage))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchSheetPreview() {
    LongDogTrackerPrimaryTheme {
        HandleSearchState(state = SearchViewModel.SearchUIState.Loading) {

        }
    }
}