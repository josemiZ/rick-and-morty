package com.mickyzg.rickandmorty.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for a single episode returned by the Rick and Morty API.
 *
 * Maps directly to the JSON structure from:
 *  - `GET /api/episode` (list item inside `results[]`)
 *  - `GET /api/episode/{id}` (single episode detail)
 *
 * Example JSON:
 * ```json
 * {
 *   "id": 1,
 *   "name": "Pilot",
 *   "air_date": "December 2, 2013",
 *   "episode": "S01E01",
 *   "characters": [
 *     "https://rickandmortyapi.com/api/character/1",
 *     "https://rickandmortyapi.com/api/character/2"
 *   ],
 *   "url": "https://rickandmortyapi.com/api/episode/1",
 *   "created": "2017-11-10T12:56:33.798Z"
 * }
 * ```
 *
 * @property id stable unique identifier.
 * @property name episode title.
 * @property airDate human-readable air date (e.g. `"December 2, 2013"`).
 * @property episodeCode season/episode code in `SxxExx` format (e.g. `"S01E01"`).
 * @property characters list of character URLs that appear in this episode.
 * @property url absolute URL of this episode resource.
 * @property created ISO-8601 timestamp of when this entry was created.
 */
data class EpisodeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("air_date") val airDate: String,
    @SerializedName("episode") val episodeCode: String,
    @SerializedName("characters") val characters: List<String>,
    @SerializedName("url") val url: String,
    @SerializedName("created") val created: String
)

