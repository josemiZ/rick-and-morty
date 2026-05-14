package com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail

import androidx.lifecycle.SavedStateHandle
import com.mickyzg.rickandmorty.domain.repository.EpisodeRepository
import com.mickyzg.rickandmorty.presentation.base.StateViewModel
import com.mickyzg.rickandmorty.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the episode detail screen.
 * Extends [StateViewModel] with [EpisodeDetailUiState] and [EpisodeDetailAction].
 */
@HiltViewModel
class EpisodeDetailViewModel @Inject constructor(
    private val repository: EpisodeRepository,
    savedStateHandle: SavedStateHandle
) : StateViewModel<EpisodeDetailUiState, EpisodeDetailAction>(EpisodeDetailUiState(isLoading = true)) {

    private val episodeId: Int =
        checkNotNull(savedStateHandle[Route.EpisodeDetail.ARG_EPISODE_ID]) {
            "EpisodeDetailViewModel requires a valid episodeId in SavedStateHandle"
        }

    private var detailFetchAttempted = false

    init {
        observeAndFetchIfNeeded()
    }

    override fun publish(action: EpisodeDetailAction) = when (action) {
        is EpisodeDetailAction.Retry -> retry()
    }

    private fun observeAndFetchIfNeeded() {
        launch {
            repository.observeEpisodeById(episodeId).collect { episode ->
                update { state -> state.copy(episode = episode, isLoading = state.isLoading && episode == null) }
                if (!detailFetchAttempted && (episode == null || !episode.isDetailLoaded)) {
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

    private fun fetchDetail() {
        launch {
            update { it.copy(isLoading = it.episode == null, error = null) }
            repository.refreshEpisodeById(episodeId)
                .onSuccess { update { it.copy(isLoading = false) } }
                .onFailure { e -> update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
