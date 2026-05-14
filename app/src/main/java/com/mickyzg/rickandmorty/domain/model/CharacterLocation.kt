package com.mickyzg.rickandmorty.domain.model

/**
 * Lightweight location reference embedded in a [Character].
 *
 * Used for both the `origin` (where the character is from) and
 * `location` (last known location) fields.
 *
 * @property name human-readable location name; may be `"unknown"`.
 * @property url absolute URL of the full location resource; empty string when unknown.
 */
data class CharacterLocation(
    val name: String,
    val url: String
)

