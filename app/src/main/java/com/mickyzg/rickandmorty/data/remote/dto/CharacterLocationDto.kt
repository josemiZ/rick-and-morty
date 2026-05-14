package com.mickyzg.rickandmorty.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Lightweight location reference embedded inside [CharacterDto].
 *
 * Both `origin` and `location` fields on a character share this same structure.
 *
 * Example JSON:
 * ```json
 * { "name": "Earth (C-137)", "url": "https://rickandmortyapi.com/api/location/1" }
 * ```
 *
 * @property name human-readable location name; `"unknown"` when not available.
 * @property url absolute URL of the full location resource; empty string when unknown.
 */
data class CharacterLocationDto(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

