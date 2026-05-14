package com.mickyzg.rickandmorty.presentation.viewmodel.episodeList

import com.mickyzg.rickandmorty.domain.repository.EpisodeRepository
import com.mickyzg.rickandmorty.presentation.base.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel for the episode list screen.
 * Extends [StateViewModel] with [EpisodeListUiState] and [EpisodeListAction].
 */
@HiltViewModel
class EpisodeListViewModel @Inject constructor(
    private val repository: EpisodeRepository
) : StateViewModel<EpisodeListUiState, EpisodeListAction>(EpisodeListUiState()) {

    private val searchQuery = MutableStateFlow("")
    private var currentPage = 1
    private var loadPageJob: Job? = null
    private var remoteSearchJob: Job? = null

    init {
        observeWithSearch()
        loadInitialIfEmpty()
    }

    override fun publish(action: EpisodeListAction) = when (action) {
        is EpisodeListAction.LoadMore -> loadMore()
        is EpisodeListAction.Refresh -> refresh()
        is EpisodeListAction.Retry -> retry()
        is EpisodeListAction.SearchQueryChanged -> searchQueryChanged(action.query)
    }

    @Suppress("OPT_IN_USAGE")
    private fun observeWithSearch() {
        launch {
            searchQuery
                .flatMapLatest { query ->
                    if (query.isBlank()) repository.observeEpisodes()
                    else repository.searchEpisodes(query)
                }
                .collect { episodes ->
                    update { state -> state.copy(episodes = episodes, isLoading = state.isLoading && episodes.isEmpty()) }
                }
        }
    }

    private fun loadInitialIfEmpty() {
        launch { if (repository.observeEpisodes().first().isEmpty()) loadPage(1) }
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
            repository.loadEpisodesPage(1, searchQuery.value.takeIf { it.isNotBlank() })
                .onSuccess { update { it.copy(isRefreshing = false) } }
                .onFailure { e -> update { it.copy(isRefreshing = false, error = e.message) } }
        }
    }

    private fun retry() {
        update { it.copy(error = null) }
        val page = if (state.value.episodes.isEmpty()) 1 else currentPage + 1
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
            val local = repository.searchEpisodes(query).first()
            if (local.isEmpty()) { currentPage = 1; loadPage(1, query) }
        }
    }

    private fun loadPage(page: Int, name: String? = null) {
        loadPageJob?.cancel()
        val isEmpty = state.value.episodes.isEmpty()
        update { it.copy(isLoading = page == 1 && isEmpty, isLoadingMore = page > 1) }
        loadPageJob = launch {
            repository.loadEpisodesPage(page, name)
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
