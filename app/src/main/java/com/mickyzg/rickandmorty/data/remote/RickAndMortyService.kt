package com.mickyzg.rickandmorty.data.remote

import com.mickyzg.rickandmorty.data.remote.dto.ApiResponse
import com.mickyzg.rickandmorty.data.remote.dto.CharacterDto
import com.mickyzg.rickandmorty.data.remote.dto.EpisodeDto
import com.mickyzg.rickandmorty.data.remote.dto.LocationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service contract for the Rick and Morty REST API.
 *
 * Base URL: [ApiConstants.BASE_URL]
 *
 * All functions are suspending so they integrate directly with coroutines.
 * They return [Response] to allow the caller (repository) to inspect the
 * HTTP status code and body separately before mapping to domain models.
 *
 * The optional [name] query parameter is omitted from the request when `null`,
 * which is the default Retrofit behavior for nullable `@Query` parameters.
 */
interface RickAndMortyService {

    // ─── Characters ──────────────────────────────────────────────────────────

    /**
     * Fetches a paginated list of characters.
     *
     * @param page 1-based page number (API has ~42 pages of 20 items each).
     * @param name optional name filter for remote search; `null` = no filter.
     */
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null
    ): Response<ApiResponse<CharacterDto>>

    /**
     * Fetches a single character by its unique id.
     */
    @GET("character/{id}")
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): Response<CharacterDto>

    // ─── Locations ───────────────────────────────────────────────────────────

    /**
     * Fetches a paginated list of locations.
     *
     * @param page 1-based page number.
     * @param name optional name filter; `null` = no filter.
     */
    @GET("location")
    suspend fun getLocations(
        @Query("page") page: Int,
        @Query("name") name: String? = null
    ): Response<ApiResponse<LocationDto>>

    /**
     * Fetches a single location by its unique id.
     */
    @GET("location/{id}")
    suspend fun getLocationById(
        @Path("id") id: Int
    ): Response<LocationDto>

    // ─── Episodes ────────────────────────────────────────────────────────────

    /**
     * Fetches a paginated list of episodes.
     *
     * @param page 1-based page number.
     * @param name optional name filter; `null` = no filter.
     */
    @GET("episode")
    suspend fun getEpisodes(
        @Query("page") page: Int,
        @Query("name") name: String? = null
    ): Response<ApiResponse<EpisodeDto>>

    /**
     * Fetches a single episode by its unique id.
     */
    @GET("episode/{id}")
    suspend fun getEpisodeById(
        @Path("id") id: Int
    ): Response<EpisodeDto>
}
