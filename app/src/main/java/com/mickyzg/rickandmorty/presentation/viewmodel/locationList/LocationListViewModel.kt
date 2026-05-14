package com.mickyzg.rickandmorty.presentation.viewmodel.locationList

import com.mickyzg.rickandmorty.domain.repository.LocationRepository
import com.mickyzg.rickandmorty.presentation.base.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel for the location list screen.
 * Extends [StateViewModel] with [LocationListUiState] and [LocationListAction].
 */
@HiltViewModel
class LocationListViewModel @Inject constructor(
    private val repository: LocationRepository
) : StateViewModel<LocationListUiState, LocationListAction>(LocationListUiState()) {

    private val searchQuery = MutableStateFlow("")
    private var currentPage = 1
    private var loadPageJob: Job? = null
    private var remoteSearchJob: Job? = null

    init {
        observeWithSearch()
        loadInitialIfEmpty()
    }

    override fun publish(action: LocationListAction) = when (action) {
        is LocationListAction.LoadMore -> loadMore()
        is LocationListAction.Refresh -> refresh()
        is LocationListAction.Retry -> retry()
        is LocationListAction.SearchQueryChanged -> searchQueryChanged(action.query)
    }

    @Suppress("OPT_IN_USAGE")
    private fun observeWithSearch() {
        launch {
            searchQuery
                .flatMapLatest { query ->
                    if (query.isBlank()) repository.observeLocations()
                    else repository.searchLocations(query)
                }
                .collect { locations ->
                    update { state -> state.copy(locations = locations, isLoading = state.isLoading && locations.isEmpty()) }
                }
        }
    }

    private fun loadInitialIfEmpty() {
        launch { if (repository.observeLocations().first().isEmpty()) loadPage(1) }
    }

    private fun loadMore() {
        val current = state.value
        if (current.isLoadingMore || current.isLoading || current.endReached) return
        loadPage(currentPage + 1, searchQuery.value.takeIf { it.isNotBlank() })
    }

    private fun refresh() {
        currentPage = 1
        update { it.copy(isRefreshing = true, endReached = false, error = null) }
        launch {
            repository.loadLocationsPage(1, searchQuery.value.takeIf { it.isNotBlank() })
                .onSuccess { update { it.copy(isRefreshing = false) } }
                .onFailure { e -> update { it.copy(isRefreshing = false, error = e.message) } }
        }
    }

    private fun retry() {
        update { it.copy(error = null) }
        val page = if (state.value.locations.isEmpty()) 1 else currentPage + 1
        loadPage(page, searchQuery.value.takeIf { it.isNotBlank() })
    }

    private fun searchQueryChanged(query: String) {
        searchQuery.value = query
        update { it.copy(searchQuery = query, endReached = false, error = null) }
        if (query.isBlank()) { currentPage = 1; remoteSearchJob?.cancel(); return }
        remoteSearchJob?.cancel()
        remoteSearchJob = launch {
            delay(SEARCH_DEBOUNCE_MS)
            if (searchQuery.value != query) return@launch
            val local = repository.searchLocations(query).first()
            if (local.isEmpty()) { currentPage = 1; loadPage(1, query) }
        }
    }

    private fun loadPage(page: Int, name: String? = null) {
        loadPageJob?.cancel()
        val isEmpty = state.value.locations.isEmpty()
        update { it.copy(isLoading = page == 1 && isEmpty, isLoadingMore = page > 1) }
        loadPageJob = launch {
            repository.loadLocationsPage(page, name)
                .onSuccess { count ->
                    currentPage = page
                    update { it.copy(isLoading = false, isLoadingMore = false, endReached = count == 0, error = null) }
                }
                .onFailure { e ->
                    update { it.copy(isLoading = false, isLoadingMore = false, error = e.message ?: "Unexpected error.") }
                }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
