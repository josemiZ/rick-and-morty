package com.mickyzg.rickandmorty.presentation.viewmodel.characterList

import com.mickyzg.rickandmorty.domain.repository.CharacterRepository
import com.mickyzg.rickandmorty.presentation.base.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel for the character list screen.
 *
 * Extends [StateViewModel] with [CharacterListUiState] and [CharacterListAction].
 * The UI sends user intents exclusively through [publish]; all handler functions
 * are private, keeping the public API minimal and auditable.
 *
 * Internal setup (observation + initial load) happens in [init] and is not
 * routed through [publish] because it is not a user action.
 */
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val repository: CharacterRepository
) : StateViewModel<CharacterListUiState, CharacterListAction>(CharacterListUiState()) {

    private val searchQuery = MutableStateFlow("")
    private var currentPage = 1
    private var loadPageJob: Job? = null
    private var remoteSearchJob: Job? = null

    init {
        observeWithSearch()
        loadInitialIfEmpty()
    }

    // ── MVI entry point ───────────────────────────────────────────────────────

    override fun publish(action: CharacterListAction) = when (action) {
        is CharacterListAction.LoadMore -> loadMore()
        is CharacterListAction.Refresh -> refresh()
        is CharacterListAction.Retry -> retry()
        is CharacterListAction.SearchQueryChanged -> searchQueryChanged(action.query)
        is CharacterListAction.ToggleFavorite -> toggleFavorite(action.characterId, action.isFavorite)
    }

    // ── Internal setup (not user actions) ────────────────────────────────────

    @Suppress("OPT_IN_USAGE")
    private fun observeWithSearch() {
        launch {
            searchQuery
                .flatMapLatest { query ->
                    if (query.isBlank()) repository.observeCharacters()
                    else repository.searchCharacters(query)
                }
                .collect { characters ->
                    update { state ->
                        state.copy(
                            characters = characters,
                            isLoading = state.isLoading && characters.isEmpty()
                        )
                    }
                }
        }
    }

    private fun loadInitialIfEmpty() {
        launch {
            val cached = repository.observeCharacters().first()
            if (cached.isEmpty()) loadPage(page = 1)
        }
    }

    // ── Action handlers (private) ─────────────────────────────────────────────

    private fun loadMore() {
        val current = state.value
        if (current.isLoadingMore || current.isLoading || current.endReached) return
        loadPage(page = currentPage + 1, name = searchQuery.value.takeIf { it.isNotBlank() })
    }

    private fun refresh() {
        currentPage = 1
        update { it.copy(isRefreshing = true, endReached = false, error = null) }
        launch {
            repository.loadCharactersPage(page = 1, name = searchQuery.value.takeIf { it.isNotBlank() })
                .onSuccess { update { it.copy(isRefreshing = false) } }
                .onFailure { e -> update { it.copy(isRefreshing = false, error = e.message) } }
        }
    }

    private fun retry() {
        update { it.copy(error = null) }
        val retryPage = if (state.value.characters.isEmpty()) 1 else currentPage + 1
        loadPage(page = retryPage, name = searchQuery.value.takeIf { it.isNotBlank() })
    }

    private fun searchQueryChanged(query: String) {
        searchQuery.value = query
        update { it.copy(searchQuery = query, endReached = false, error = null) }

        if (query.isBlank()) {
            currentPage = 1
            remoteSearchJob?.cancel()
            return
        }

        remoteSearchJob?.cancel()
        remoteSearchJob = launch {
            delay(SEARCH_DEBOUNCE_MS)
            if (searchQuery.value != query) return@launch
            val local = repository.searchCharacters(query).first()
            if (local.isEmpty()) {
                currentPage = 1
                loadPage(page = 1, name = query)
            }
        }
    }

    private fun toggleFavorite(characterId: Int, isFavorite: Boolean) {
        launch {
            repository.setFavorite(characterId, isFavorite)
                .onFailure { e -> update { it.copy(error = e.message) } }
        }
    }

    // ── Shared page loader ────────────────────────────────────────────────────

    private fun loadPage(page: Int, name: String? = null) {
        loadPageJob?.cancel()
        val isEmpty = state.value.characters.isEmpty()
        update { it.copy(isLoading = page == 1 && isEmpty, isLoadingMore = page > 1) }
        loadPageJob = launch {
            repository.loadCharactersPage(page, name)
                .onSuccess { count ->
                    currentPage = page
                    update { it.copy(isLoading = false, isLoadingMore = false, endReached = count == 0, error = null) }
                }
                .onFailure { e ->
                    update { it.copy(isLoading = false, isLoadingMore = false, error = e.message ?: "Unexpected error. Please try again.") }
                }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
