package com.mickyzg.rickandmorty.presentation.component

/**
 * Centralised test tag constants for Compose UI integration tests.
 *
 * Using a single object avoids magic strings scattered across production and test code.
 * Test code imports these constants to find nodes via `onNodeWithTag(TestTags.XXX)`.
 */
object TestTags {
    // ── CharacterListScreen ──────────────────────────────────────────────────
    const val SEARCH_FIELD = "search_field"
    const val CHARACTER_LIST = "character_list"

    // ── LocationListScreen ────────────────────────────────────────────────────
    const val LOCATION_SEARCH_FIELD = "location_search_field"
    const val LOCATION_LIST = "location_list"
    fun locationCard(id: Int) = "location_card_$id"

    // ── EpisodeListScreen ─────────────────────────────────────────────────────
    const val EPISODE_SEARCH_FIELD = "episode_search_field"
    const val EPISODE_LIST = "episode_list"
    fun episodeCard(id: Int) = "episode_card_$id"

    // ── FavoritesScreen ──────────────────────────────────────────────────────
    const val FAVORITES_LIST = "favorites_list"

    // ── CharacterCard ────────────────────────────────────────────────────────
    fun characterCard(id: Int) = "character_card_$id"
    fun favoriteButton(id: Int) = "favorite_btn_$id"

    // ── Shared reusable components ───────────────────────────────────────────
    const val EMPTY_CONTENT = "empty_content"
    const val ERROR_CONTENT = "error_content"
    const val LOADING_INDICATOR = "loading_indicator"
}

