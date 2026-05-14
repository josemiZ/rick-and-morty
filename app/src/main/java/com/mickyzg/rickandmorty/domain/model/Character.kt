package com.mickyzg.rickandmorty.domain.model

/**
 * Domain representation of a Rick and Morty character.
 *
 * Split into two conceptual zones:
 *  - **List fields** (always populated after a page load): [id], [name], [status],
 *    [species], [imageUrl], [isFavorite].
 *  - **Detail fields** (populated only after [refreshCharacterById] is called):
 *    [type], [gender], [origin], [location], [episodeUrls], [createdAtIso].
 *    These are nullable until the detail has been fetched and persisted locally.
 *
 * [isDetailLoaded] lets the ViewModel decide whether to trigger a remote refresh
 * when the user navigates to the detail screen.
 *
 * @property id stable unique identifier from the API.
 * @property name character name (e.g. "Rick Sanchez").
 * @property status life status.
 * @property species biological species (e.g. "Human", "Alien").
 * @property imageUrl absolute URL to the character avatar.
 * @property isFavorite whether the user has marked this character as favorite locally.
 * @property type sub-species or variant; `null` until detail is loaded.
 * @property gender gender classification; `null` until detail is loaded.
 * @property origin where the character originated; `null` until detail is loaded.
 * @property location current known location; `null` until detail is loaded.
 * @property episodeUrls URLs of episodes the character appears in; `null` until detail is loaded.
 * @property createdAtIso ISO-8601 creation timestamp; `null` until detail is loaded.
 * @property isDetailLoaded `true` once all detail fields have been fetched and stored.
 */
data class Character(
    // ── List fields ──────────────────────────────────────────────────────────
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val imageUrl: String,
    val isFavorite: Boolean = false,
    // ── Detail fields (nullable until fetched) ───────────────────────────────
    val type: String? = null,
    val gender: CharacterGender? = null,
    val origin: CharacterLocation? = null,
    val location: CharacterLocation? = null,
    val episodeUrls: List<String>? = null,
    val createdAtIso: String? = null,
    val isDetailLoaded: Boolean = false
)
