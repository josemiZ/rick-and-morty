package com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail

import androidx.lifecycle.SavedStateHandle
import com.mickyzg.rickandmorty.domain.repository.LocationRepository
import com.mickyzg.rickandmorty.presentation.base.StateViewModel
import com.mickyzg.rickandmorty.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the location detail screen.
 * Extends [StateViewModel] with [LocationDetailUiState] and [LocationDetailAction].
 */
@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val repository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : StateViewModel<LocationDetailUiState, LocationDetailAction>(LocationDetailUiState(isLoading = true)) {

    private val locationId: Int =
        checkNotNull(savedStateHandle[Route.LocationDetail.ARG_LOCATION_ID]) {
            "LocationDetailViewModel requires a valid locationId in SavedStateHandle"
        }

    private var detailFetchAttempted = false

    init {
        observeAndFetchIfNeeded()
    }

    override fun publish(action: LocationDetailAction) = when (action) {
        is LocationDetailAction.Retry -> retry()
    }

    private fun observeAndFetchIfNeeded() {
        launch {
            repository.observeLocationById(locationId).collect { location ->
                update { state -> state.copy(location = location, isLoading = state.isLoading && location == null) }
                if (!detailFetchAttempted && (location == null || !location.isDetailLoaded)) {
                    detailFetchAttempted = true
                    fetchDetail()
                }
            }
        }
    }

    private fun retry() {
        detailFetchAttempted = false
        update { it.copy(error = null) }
        fetchDetail()
    }

    private fun fetchDetail() {
        launch {
            update { it.copy(isLoading = it.location == null, error = null) }
            repository.refreshLocationById(locationId)
                .onSuccess { update { it.copy(isLoading = false) } }
                .onFailure { e -> update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
