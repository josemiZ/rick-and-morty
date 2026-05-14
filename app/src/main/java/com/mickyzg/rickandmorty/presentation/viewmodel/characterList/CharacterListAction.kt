package com.mickyzg.rickandmorty.presentation.viewmodel.characterList

/**
 * Exhaustive set of user intents for the character list screen.
 *
 * Every possible interaction the UI can trigger is expressed here.
 * The ViewModel's [CharacterListViewModel.publish] uses an exhaustive `when`
 * on this sealed interface, ensuring no action is silently unhandled.
 */
sealed interface CharacterListAction {
    /** Load the next pagination page. No-op when already loading or end is reached. */
    data object LoadMore : CharacterListAction
    /** Pull-to-refresh: re-load page 1 while keeping the existing list visible. */
    data object Refresh : CharacterListAction
    /** Retry the last failed page load. */
    data object Retry : CharacterListAction
    /** User updated the search field. Empty string clears the search. */
    data class SearchQueryChanged(val query: String) : CharacterListAction
    /** Toggle the favorite flag for a character in the list. */
    data class ToggleFavorite(val characterId: Int, val isFavorite: Boolean) : CharacterListAction
}

