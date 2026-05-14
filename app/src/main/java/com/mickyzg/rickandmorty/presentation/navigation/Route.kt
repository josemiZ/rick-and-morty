package com.mickyzg.rickandmorty.presentation.navigation

/**
 * Type-safe navigation routes for the app.
 *
 * Top-level destinations ([CharacterList], [Favorites], [LocationList], [EpisodeList])
 * are exposed through the bottom navigation bar. Detail routes are pushed onto the
 * back stack when the user taps a list item.
 */
sealed class Route(val path: String) {

    data object CharacterList : Route("character_list")

    data object Favorites : Route("favorites")

    data object LocationList : Route("location_list")

    data object EpisodeList : Route("episode_list")

    data object CharacterDetail : Route("character_detail/{characterId}") {
        const val ARG_CHARACTER_ID = "characterId"
        fun build(characterId: Int): String = "character_detail/$characterId"
    }

    data object LocationDetail : Route("location_detail/{locationId}") {
        const val ARG_LOCATION_ID = "locationId"
        fun build(locationId: Int): String = "location_detail/$locationId"
    }

    data object EpisodeDetail : Route("episode_detail/{episodeId}") {
        const val ARG_EPISODE_ID = "episodeId"
        fun build(episodeId: Int): String = "episode_detail/$episodeId"
    }
}
