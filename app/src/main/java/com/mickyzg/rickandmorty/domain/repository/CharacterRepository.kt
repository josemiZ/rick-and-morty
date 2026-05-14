package com.mickyzg.rickandmorty.domain.repository

import com.mickyzg.rickandmorty.domain.model.Character
import kotlinx.coroutines.flow.Flow

/**
 * Single contract that the presentation layer depends on for any character-related data.
 *
 * Designed for an **offline-first** architecture:
 *  - `observe*` methods expose reactive streams from the **local** source of truth (Room).
 *  - `refresh*` and `loadNextPage` are one-shot suspend operations that hit the **remote**
 *    API and persist the result locally; UI observers receive the changes automatically
 *    through the `observe*` flows.
 *  - `setFavorite` mutates only the local store; favorites are not synced server-side.
 *
 * NOTE: The concrete implementation lives in [com.mickyzg.rickandmorty.data.repository]
 * and will be wired to the interface via Hilt's `DataModule` in a later ticket.
 */
interface CharacterRepository {

    /**
     * Streams the cached list of characters in the order they were loaded from the API.
     * Emits a fresh list whenever the local database changes (new page, favorite toggle, etc.).
     */
    fun observeCharacters(): Flow<List<Character>>

    /**
     * Fetches a page of characters from the remote API and persists it locally.
     *
     * @param page 1-based page number expected by the API.
     * @param name optional name filter; `null` means no filter (full list).
     *   When provided, the API returns only characters whose name contains [name].
     *   HTTP 404 (no matches) is treated as an empty result, not an error.
     * @return [Result.success] with the number of new items inserted, or [Result.failure]
     * with the underlying network/parsing error.
     */
    suspend fun loadCharactersPage(page: Int, name: String? = null): Result<Int>

    /**
     * Streams a single character from the local store, or `null` if it is not cached.
     */
    fun observeCharacterById(id: Int): Flow<Character?>

    /**
     * Fetches a single character by id from the remote API and persists it locally.
     * Useful when navigating to detail before the list page that contains it has loaded.
     */
    suspend fun refreshCharacterById(id: Int): Result<Unit>

    /**
     * Streams characters whose name matches [query] (case-insensitive, local search).
     * Emits an empty list when [query] is blank.
     */
    fun searchCharacters(query: String): Flow<List<Character>>

    /**
     * Streams every character currently flagged as favorite by the user.
     */
    fun observeFavorites(): Flow<List<Character>>

    /**
     * Persists the favorite flag for the given character.
     *
     * @return [Result.success] on write, [Result.failure] if the character is not cached
     * or the write fails.
     */
    suspend fun setFavorite(characterId: Int, isFavorite: Boolean): Result<Unit>
}

