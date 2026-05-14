package com.mickyzg.rickandmorty.domain.repository

import com.mickyzg.rickandmorty.domain.model.Episode
import kotlinx.coroutines.flow.Flow

/**
 * Contract for episode data operations.
 *
 * Follows the same offline-first pattern as [CharacterRepository]:
 *  - `observe*` methods expose reactive streams from Room (local source of truth).
 *  - `load*` / `refresh*` are one-shot suspend operations that hit the remote API
 *    and persist locally; observers receive updates automatically.
 */
interface EpisodeRepository {

    /** Streams the cached list of episodes ordered by page load sequence. */
    fun observeEpisodes(): Flow<List<Episode>>

    /**
     * Fetches a page of episodes from the remote API and persists it locally.
     *
     * @param page 1-based page number.
     * @param name optional name filter; `null` means no filter.
     * @return [Result.success] with new item count, or [Result.failure] on error.
     */
    suspend fun loadEpisodesPage(page: Int, name: String? = null): Result<Int>

    /** Streams a single episode from local store, or `null` if not cached. */
    fun observeEpisodeById(id: Int): Flow<Episode?>

    /**
     * Fetches a single episode detail from the remote API and persists locally.
     * Used when navigating to detail before the list page containing it has loaded.
     */
    suspend fun refreshEpisodeById(id: Int): Result<Unit>

    /**
     * Streams episodes whose name matches [query] (case-insensitive, local search).
     * Emits empty list when [query] is blank.
     */
    fun searchEpisodes(query: String): Flow<List<Episode>>
}

