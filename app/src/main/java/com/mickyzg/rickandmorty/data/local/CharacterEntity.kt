package com.mickyzg.rickandmorty.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a character stored in the local database.
 *
 * Fields are split into two zones matching the domain model strategy:
 *  - **List fields**: always present after a page load.
 *  - **Detail fields**: nullable until [isDetailLoaded] is `true` (set after detail fetch).
 *
 * [pageIndex] preserves the original API pagination order for consistent list display.
 * [isFavorite] is the only user-mutated field and persists independently of remote refreshes.
 */
@Entity(tableName = "characters")
data class CharacterEntity(
    // ── Identity & list fields ────────────────────────────────────────────────
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val imageUrl: String,
    val pageIndex: Int = 0,
    val isFavorite: Boolean = false,
    // ── Detail fields (null until fetched) ───────────────────────────────────
    val type: String? = null,
    val gender: String? = null,
    val originName: String? = null,
    val originUrl: String? = null,
    val locationName: String? = null,
    val locationUrl: String? = null,
    val episodeUrlsJson: String? = null,
    val createdAtIso: String? = null,
    val isDetailLoaded: Boolean = false
)
