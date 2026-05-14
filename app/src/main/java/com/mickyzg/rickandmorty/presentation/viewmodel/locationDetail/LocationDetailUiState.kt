package com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail

import com.mickyzg.rickandmorty.domain.model.Location

/**
 * UI state for the location detail screen.
 *
 * @property location the location being shown; `null` until loaded from cache or remote.
 * @property isLoading `true` while fetching the location (initial or retry).
 * @property error non-null when the last fetch operation failed.
 */
data class LocationDetailUiState(
    val location: Location? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

