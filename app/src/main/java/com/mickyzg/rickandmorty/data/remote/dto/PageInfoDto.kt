package com.mickyzg.rickandmorty.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents the pagination metadata returned by every paginated endpoint
 * of the Rick and Morty API.
 *
 * Example JSON:
 * ```json
 * {
 *   "count": 826,
 *   "pages": 42,
 *   "next": "https://rickandmortyapi.com/api/character?page=2",
 *   "prev": null
 * }
 * ```
 *
 * @property count total number of items across all pages.
 * @property pages total number of pages.
 * @property next absolute URL of the next page, or `null` if on the last page.
 * @property prev absolute URL of the previous page, or `null` if on the first page.
 */
data class PageInfoDto(
    @SerializedName("count") val count: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("prev") val prev: String?
)

