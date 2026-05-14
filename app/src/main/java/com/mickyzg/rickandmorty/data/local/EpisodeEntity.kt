package com.mickyzg.rickandmorty.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an episode stored in the local database.
 *
 * Fields are split into two zones matching the domain model strategy:
 *  - **List fields**: always present after a page load.
 *  - **Detail fields**: nullable until [isDetailLoaded] is `true` (set after detail fetch).
 *
 * [pageIndex] preserves the original API pagination order for consistent list display.
 * [characterUrlsJson] stores the list of character URLs as a comma-separated string.
 */
@Entity(tableName = "episodes")
data class EpisodeEntity(
    // ── Identity & list fields ────────────────────────────────────────────────
    @PrimaryKey val id: Int,
    val name: String,
    val airDate: String,
    val episodeCode: String,
    val pageIndex: Int = 0,
    // ── Detail fields (null until fetched) ───────────────────────────────────
    val characterUrlsJson: String? = null,
    val url: String? = null,
    val createdAtIso: String? = null,
    val isDetailLoaded: Boolean = false
)

