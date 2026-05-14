package com.mickyzg.rickandmorty.presentation.viewmodel.locationList

import com.mickyzg.rickandmorty.domain.model.Location

/**
 * UI state for the location list screen.
 *
 * @property locations current visible list of locations (from local cache).
 * @property isLoading `true` only during the very first load when the list is empty.
 * @property isLoadingMore `true` when fetching the next pagination page.
 * @property isRefreshing `true` during a pull-to-refresh operation.
 * @property endReached `true` when the API reports no more pages.
 * @property error non-null when the last network operation failed.
 * @property searchQuery current value of the search field.
 */
data class LocationListUiState(
    val locations: List<Location> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

