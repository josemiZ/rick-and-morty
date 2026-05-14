package com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail

import com.mickyzg.rickandmorty.domain.model.Episode

/**
 * UI state for the episode detail screen.
 *
 * @property episode the episode being shown; `null` until loaded from cache or remote.
 * @property isLoading `true` while fetching the episode (initial or retry).
 * @property error non-null when the last fetch operation failed.
 */
data class EpisodeDetailUiState(
    val episode: Episode? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

