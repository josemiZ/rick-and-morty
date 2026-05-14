package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mickyzg.rickandmorty.presentation.component.EmptyContent
import com.mickyzg.rickandmorty.presentation.component.EpisodeCard
import com.mickyzg.rickandmorty.presentation.component.ErrorContent
import com.mickyzg.rickandmorty.presentation.component.LoadingIndicator
import com.mickyzg.rickandmorty.presentation.component.ShimmerCardList
import com.mickyzg.rickandmorty.presentation.screen.preview.EpisodeListPreviewProvider
import com.mickyzg.rickandmorty.presentation.screen.preview.ThemePreviews
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeList.EpisodeListAction
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeList.EpisodeListUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme

private const val LOAD_MORE_THRESHOLD = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeListScreen(
    uiState: EpisodeListUiState,
    onAction: (EpisodeListAction) -> Unit,
    onEpisodeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) onAction(EpisodeListAction.LoadMore) }
    LaunchedEffect(uiState.error) {
        if (uiState.error != null && uiState.episodes.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.error)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Episodes") })
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { onAction(EpisodeListAction.SearchQueryChanged(it)) },
                    placeholder = { Text("Search episodes…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onAction(EpisodeListAction.SearchQueryChanged("")) }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { onAction(EpisodeListAction.Refresh) },
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> ShimmerCardList()
                uiState.error != null && uiState.episodes.isEmpty() ->
                    ErrorContent(message = uiState.error, onRetry = { onAction(EpisodeListAction.Retry) })
                uiState.episodes.isEmpty() ->
                    EmptyContent(
                        title = "No episodes found",
                        subtitle = if (uiState.searchQuery.isNotBlank())
                            "No results for \"${uiState.searchQuery}\"" else "Pull down to load episodes."
                    )
                else -> LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(items = uiState.episodes, key = { it.id }) { episode ->
                        EpisodeCard(episode = episode, onClick = { onEpisodeClick(episode.id) })
                    }
                    if (uiState.isLoadingMore) item { LoadingIndicator() }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun EpisodeListScreenPreview(
    @PreviewParameter(EpisodeListPreviewProvider::class) uiState: EpisodeListUiState
) {
    RickAndMortyTheme {
        EpisodeListScreen(
            uiState = uiState,
            onAction = {},
            onEpisodeClick = {}
        )
    }
}
