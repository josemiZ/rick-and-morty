package com.mickyzg.rickandmorty.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for a single location returned by the Rick and Morty API.
 *
 * Maps directly to the JSON structure from:
 *  - `GET /api/location` (list item inside `results[]`)
 *  - `GET /api/location/{id}` (single location detail)
 *
 * Example JSON:
 * ```json
 * {
 *   "id": 1,
 *   "name": "Earth (C-137)",
 *   "type": "Planet",
 *   "dimension": "Dimension C-137",
 *   "residents": [
 *     "https://rickandmortyapi.com/api/character/1",
 *     "https://rickandmortyapi.com/api/character/2"
 *   ],
 *   "url": "https://rickandmortyapi.com/api/location/1",
 *   "created": "2017-11-10T12:42:04.162Z"
 * }
 * ```
 *
 * @property id stable unique identifier.
 * @property name human-readable location name.
 * @property type type of location (e.g. `"Planet"`, `"Space station"`); empty if unknown.
 * @property dimension dimension the location belongs to (e.g. `"Dimension C-137"`); empty if unknown.
 * @property residents list of character URLs that are residents of this location.
 * @property url absolute URL of this location resource.
 * @property created ISO-8601 timestamp of when this entry was created.
 */
data class LocationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("dimension") val dimension: String,
    @SerializedName("residents") val residents: List<String>,
    @SerializedName("url") val url: String,
    @SerializedName("created") val created: String
)

