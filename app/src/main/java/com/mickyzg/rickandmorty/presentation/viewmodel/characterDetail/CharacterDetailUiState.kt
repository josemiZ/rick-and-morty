package com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail

import com.mickyzg.rickandmorty.domain.model.Character

/**
 * UI state for the character detail screen.
 *
 * @property character the character being shown; `null` until loaded from cache or remote.
 * @property isLoading `true` while fetching the character (initial or retry).
 * @property error non-null when the last fetch operation failed.
 */
data class CharacterDetailUiState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

