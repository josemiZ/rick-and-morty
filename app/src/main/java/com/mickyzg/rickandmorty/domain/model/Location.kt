package com.mickyzg.rickandmorty.domain.model

/**
 * Domain representation of a Rick and Morty location.
 *
 * Split into two conceptual zones:
 *  - **List fields** (always populated after a page load): [id], [name], [type], [dimension].
 *  - **Detail fields** (populated only after [refreshLocationById] is called):
 *    [residentUrls], [url], [createdAtIso].
 *
 * [isDetailLoaded] lets the ViewModel decide whether to trigger a remote refresh
 * when the user navigates to the detail screen.
 *
 * @property id stable unique identifier from the API.
 * @property name human-readable location name (e.g. "Earth (C-137)").
 * @property type type of the location (e.g. "Planet", "Space station"); empty if unknown.
 * @property dimension dimension the location belongs to; empty if unknown.
 * @property residentUrls absolute URLs of characters residing here; `null` until detail is loaded.
 * @property url absolute URL of this location resource; `null` until detail is loaded.
 * @property createdAtIso ISO-8601 creation timestamp; `null` until detail is loaded.
 * @property isDetailLoaded `true` once all detail fields have been fetched and stored.
 */
data class Location(
    // ── List fields ──────────────────────────────────────────────────────────
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    // ── Detail fields (nullable until fetched) ───────────────────────────────
    val residentUrls: List<String>? = null,
    val url: String? = null,
    val createdAtIso: String? = null,
    val isDetailLoaded: Boolean = false
)

