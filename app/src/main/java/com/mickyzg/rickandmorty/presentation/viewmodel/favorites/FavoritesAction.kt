package com.mickyzg.rickandmorty.presentation.viewmodel.favorites

/**
 * Exhaustive set of user intents for the favorites screen.
 */
sealed interface FavoritesAction {
    /** Remove the character with [characterId] from favorites. */
    data class RemoveFavorite(val characterId: Int) : FavoritesAction
}

