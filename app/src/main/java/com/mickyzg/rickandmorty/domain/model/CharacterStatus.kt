package com.mickyzg.rickandmorty.domain.model

/**
 * Life status of a character as exposed by the Rick and Morty API.
 */
enum class CharacterStatus {
    ALIVE, DEAD, UNKNOWN;

    companion object {
        /** Maps a raw API string (case-insensitive) to a [CharacterStatus], defaulting to [UNKNOWN]. */
        fun fromRaw(raw: String?): CharacterStatus = when (raw?.lowercase()) {
            "alive" -> ALIVE
            "dead" -> DEAD
            else -> UNKNOWN
        }
    }
}

