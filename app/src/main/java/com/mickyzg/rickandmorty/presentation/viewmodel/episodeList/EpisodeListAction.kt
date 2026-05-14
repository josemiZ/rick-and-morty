package com.mickyzg.rickandmorty.presentation.viewmodel.episodeList

/**
 * Exhaustive set of user intents for the episode list screen.
 */
sealed interface EpisodeListAction {
    data object LoadMore : EpisodeListAction
    data object Refresh : EpisodeListAction
    data object Retry : EpisodeListAction
    data class SearchQueryChanged(val query: String) : EpisodeListAction
}

