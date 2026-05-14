package com.mickyzg.rickandmorty.presentation.viewmodel.locationList

/**
 * Exhaustive set of user intents for the location list screen.
 */
sealed interface LocationListAction {
    data object LoadMore : LocationListAction
    data object Refresh : LocationListAction
    data object Retry : LocationListAction
    data class SearchQueryChanged(val query: String) : LocationListAction
}

