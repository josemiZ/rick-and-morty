package com.mickyzg.rickandmorty.presentation.viewmodel.favorites

import com.mickyzg.rickandmorty.domain.repository.CharacterRepository
import com.mickyzg.rickandmorty.presentation.base.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the favorites screen.
 * Extends [StateViewModel] with [FavoritesUiState] and [FavoritesAction].
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: CharacterRepository
) : StateViewModel<FavoritesUiState, FavoritesAction>(FavoritesUiState(isLoading = true)) {

    init {
        observeFavorites()
    }

    override fun publish(action: FavoritesAction) = when (action) {
        is FavoritesAction.RemoveFavorite -> removeFavorite(action.characterId)
    }

    private fun observeFavorites() {
        launch {
            repository.observeFavorites().collect { favorites ->
                update { it.copy(favorites = favorites, isLoading = false) }
            }
        }
    }

    private fun removeFavorite(characterId: Int) {
        launch { repository.setFavorite(characterId, isFavorite = false) }
    }
}
