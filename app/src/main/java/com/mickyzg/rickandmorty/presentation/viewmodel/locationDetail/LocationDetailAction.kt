package com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail

/**
 * Exhaustive set of user intents for the location detail screen.
 */
sealed interface LocationDetailAction {
    data object Retry : LocationDetailAction
}

