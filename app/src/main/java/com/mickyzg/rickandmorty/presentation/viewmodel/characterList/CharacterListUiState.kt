package com.mickyzg.rickandmorty.presentation.viewmodel.characterList

import com.mickyzg.rickandmorty.domain.model.Character

/**
 * UI state for the character list screen.
 *
 * Using a data class (vs sealed class) allows multiple state dimensions to coexist
 * simultaneously — e.g., showing existing content while loading the next page.
 *
 * @property characters current visible list of characters (from local cache).
 * @property isLoading `true` only during the very first load when the list is empty.
 * @property isLoadingMore `true` when fetching the next pagination page.
 * @property isRefreshing `true` during a pull-to-refresh operation.
 * @property endReached `true` when the API reports no more pages.
 * @property error non-null when the last network operation failed.
 * @property searchQuery current value of the search field.
 */
data class CharacterListUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

