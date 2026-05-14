package com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail

/**
 * Exhaustive set of user intents for the character detail screen.
 */
sealed interface CharacterDetailAction {
    /** Retry fetching the character detail after a network error. */
    data object Retry : CharacterDetailAction
    /** Toggle the favorite flag on the currently displayed character. */
    data object ToggleFavorite : CharacterDetailAction
}

