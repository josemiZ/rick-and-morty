package com.mickyzg.rickandmorty.domain.repository

import com.mickyzg.rickandmorty.domain.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Contract for location data operations.
 *
 * Follows the same offline-first pattern as [CharacterRepository]:
 *  - `observe*` methods expose reactive streams from Room (local source of truth).
 *  - `load*` / `refresh*` are one-shot suspend operations that hit the remote API
 *    and persist locally; observers receive updates automatically.
 */
interface LocationRepository {

    /** Streams the cached list of locations ordered by page load sequence. */
    fun observeLocations(): Flow<List<Location>>

    /**
     * Fetches a page of locations from the remote API and persists it locally.
     *
     * @param page 1-based page number.
     * @param name optional name filter; `null` means no filter.
     * @return [Result.success] with new item count, or [Result.failure] on error.
     */
    suspend fun loadLocationsPage(page: Int, name: String? = null): Result<Int>

    /** Streams a single location from local store, or `null` if not cached. */
    fun observeLocationById(id: Int): Flow<Location?>

    /**
     * Fetches a single location detail from the remote API and persists locally.
     * Used when navigating to detail before the list page containing it has loaded.
     */
    suspend fun refreshLocationById(id: Int): Result<Unit>

    /**
     * Streams locations whose name matches [query] (case-insensitive, local search).
     * Emits empty list when [query] is blank.
     */
    fun searchLocations(query: String): Flow<List<Location>>
}

