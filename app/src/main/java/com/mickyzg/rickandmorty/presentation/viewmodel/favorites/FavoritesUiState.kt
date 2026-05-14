package com.mickyzg.rickandmorty.presentation.viewmodel.favorites

import com.mickyzg.rickandmorty.domain.model.Character

/**
 * UI state for the favorites screen.
 *
 * @property favorites the user's favorited characters, ordered alphabetically.
 * @property isLoading `true` on the initial load.
 */
data class FavoritesUiState(
    val favorites: List<Character> = emptyList(),
    val isLoading: Boolean = false
)

