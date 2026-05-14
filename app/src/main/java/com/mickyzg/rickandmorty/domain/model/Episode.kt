package com.mickyzg.rickandmorty.domain.model

/**
 * Domain representation of a Rick and Morty episode.
 *
 * Split into two conceptual zones:
 *  - **List fields** (always populated after a page load): [id], [name], [airDate], [episodeCode].
 *  - **Detail fields** (populated only after [refreshEpisodeById] is called):
 *    [characterUrls], [url], [createdAtIso].
 *
 * [isDetailLoaded] lets the ViewModel decide whether to trigger a remote refresh
 * when the user navigates to the detail screen.
 *
 * @property id stable unique identifier from the API.
 * @property name episode title (e.g. "Pilot").
 * @property airDate human-readable air date (e.g. "December 2, 2013").
 * @property episodeCode season/episode code in SxxExx format (e.g. "S01E01").
 * @property characterUrls absolute URLs of characters appearing in this episode; `null` until detail is loaded.
 * @property url absolute URL of this episode resource; `null` until detail is loaded.
 * @property createdAtIso ISO-8601 creation timestamp; `null` until detail is loaded.
 * @property isDetailLoaded `true` once all detail fields have been fetched and stored.
 */
data class Episode(
    // ── List fields ──────────────────────────────────────────────────────────
    val id: Int,
    val name: String,
    val airDate: String,
    val episodeCode: String,
    // ── Detail fields (nullable until fetched) ───────────────────────────────
    val characterUrls: List<String>? = null,
    val url: String? = null,
    val createdAtIso: String? = null,
    val isDetailLoaded: Boolean = false
)

