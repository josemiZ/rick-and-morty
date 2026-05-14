package com.mickyzg.rickandmorty.presentation.viewmodel.episodeList

import com.mickyzg.rickandmorty.domain.model.Episode

/**
 * UI state for the episode list screen.
 *
 * @property episodes current visible list of episodes (from local cache).
 * @property isLoading `true` only during the very first load when the list is empty.
 * @property isLoadingMore `true` when fetching the next pagination page.
 * @property isRefreshing `true` during a pull-to-refresh operation.
 * @property endReached `true` when the API reports no more pages.
 * @property error non-null when the last network operation failed.
 * @property searchQuery current value of the search field.
 */
data class EpisodeListUiState(
    val episodes: List<Episode> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

