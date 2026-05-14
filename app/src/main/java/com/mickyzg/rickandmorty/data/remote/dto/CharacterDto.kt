package com.mickyzg.rickandmorty.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for a single character returned by the Rick and Morty API.
 *
 * Maps directly to the JSON structure from:
 *  - `GET /api/character` (list item inside `results[]`)
 *  - `GET /api/character/{id}` (single character detail)
 *
 * Example JSON:
 * ```json
 * {
 *   "id": 1,
 *   "name": "Rick Sanchez",
 *   "status": "Alive",
 *   "species": "Human",
 *   "type": "",
 *   "gender": "Male",
 *   "origin": { "name": "Earth (C-137)", "url": "https://rickandmortyapi.com/api/location/1" },
 *   "location": { "name": "Citadel of Ricks", "url": "https://rickandmortyapi.com/api/location/3" },
 *   "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
 *   "episode": [ "https://rickandmortyapi.com/api/episode/1", "..." ],
 *   "url": "https://rickandmortyapi.com/api/character/1",
 *   "created": "2017-11-04T18:48:46.250Z"
 * }
 * ```
 *
 * @property id stable unique identifier.
 * @property name character name.
 * @property status raw life status string: `"Alive"`, `"Dead"`, or `"unknown"`.
 * @property species biological species (e.g. `"Human"`, `"Alien"`).
 * @property type sub-species / variant; empty string for most characters.
 * @property gender raw gender string: `"Female"`, `"Male"`, `"Genderless"`, or `"unknown"`.
 * @property origin location where the character was originally from.
 * @property location last known location of the character.
 * @property image absolute URL to the character's avatar image.
 * @property episode list of episode URLs the character appears in.
 * @property url absolute URL of this character resource.
 * @property created ISO-8601 timestamp of when this entry was created.
 */
data class CharacterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String,
    @SerializedName("type") val type: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("origin") val origin: CharacterLocationDto,
    @SerializedName("location") val location: CharacterLocationDto,
    @SerializedName("image") val image: String,
    @SerializedName("episode") val episode: List<String>,
    @SerializedName("url") val url: String,
    @SerializedName("created") val created: String
)

