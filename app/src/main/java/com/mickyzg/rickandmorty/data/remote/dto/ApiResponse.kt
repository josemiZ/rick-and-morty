package com.mickyzg.rickandmorty.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Generic wrapper for all paginated list responses from the Rick and Morty API.
 *
 * All three resource endpoints (character, location, episode) share the same
 * envelope structure, so a single generic class avoids duplication.
 *
 * Example JSON:
 * ```json
 * {
 *   "info": { "count": 826, "pages": 42, "next": "...", "prev": null },
 *   "results": [ { ... }, { ... } ]
 * }
 * ```
 *
 * @param T the DTO type for the items inside `results`.
 * @property info pagination metadata for the current response.
 * @property results list of resource items on the current page.
 */
data class ApiResponse<T>(
    @SerializedName("info") val info: PageInfoDto,
    @SerializedName("results") val results: List<T>
)

