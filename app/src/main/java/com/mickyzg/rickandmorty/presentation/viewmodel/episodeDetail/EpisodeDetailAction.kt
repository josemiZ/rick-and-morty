package com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail

/**
 * Exhaustive set of user intents for the episode detail screen.
 */
sealed interface EpisodeDetailAction {
    data object Retry : EpisodeDetailAction
}

