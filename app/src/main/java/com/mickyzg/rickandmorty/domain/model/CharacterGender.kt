package com.mickyzg.rickandmorty.domain.model

/**
 * Gender classification of a character as exposed by the Rick and Morty API.
 */
enum class CharacterGender {
    FEMALE, MALE, GENDERLESS, UNKNOWN;

    companion object {
        /** Maps a raw API string (case-insensitive) to a [CharacterGender], defaulting to [UNKNOWN]. */
        fun fromRaw(raw: String?): CharacterGender = when (raw?.lowercase()) {
            "female" -> FEMALE
            "male" -> MALE
            "genderless" -> GENDERLESS
            else -> UNKNOWN
        }
    }
}

