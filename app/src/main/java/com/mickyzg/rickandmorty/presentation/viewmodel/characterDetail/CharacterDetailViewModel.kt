package com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail

import androidx.lifecycle.SavedStateHandle
import com.mickyzg.rickandmorty.domain.repository.CharacterRepository
import com.mickyzg.rickandmorty.presentation.base.StateViewModel
import com.mickyzg.rickandmorty.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the character detail screen.
 * Extends [StateViewModel] with [CharacterDetailUiState] and [CharacterDetailAction].
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : StateViewModel<CharacterDetailUiState, CharacterDetailAction>(CharacterDetailUiState(isLoading = true)) {

    private val characterId: Int =
        checkNotNull(savedStateHandle[Route.CharacterDetail.ARG_CHARACTER_ID]) {
            "CharacterDetailViewModel requires a valid characterId in SavedStateHandle"
        }

    private var detailFetchAttempted = false

    init {
        observeAndFetchIfNeeded()
    }

    override fun publish(action: CharacterDetailAction) = when (action) {
        is CharacterDetailAction.Retry -> retry()
        is CharacterDetailAction.ToggleFavorite -> toggleFavorite()
    }

    private fun observeAndFetchIfNeeded() {
        launch {
            repository.observeCharacterById(characterId).collect { character ->
                update { state -> state.copy(character = character, isLoading = state.isLoading && character == null) }
                if (!detailFetchAttempted && (character == null || !character.isDetailLoaded)) {
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

    private fun toggleFavorite() {
        val character = state.value.character ?: return
        launch {
            repository.setFavorite(characterId, !character.isFavorite)
                .onFailure { e -> update { it.copy(error = e.message) } }
        }
    }

    private fun fetchDetail() {
        launch {
            update { it.copy(isLoading = it.character == null, error = null) }
            repository.refreshCharacterById(characterId)
                .onSuccess { update { it.copy(isLoading = false) } }
                .onFailure { e -> update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
