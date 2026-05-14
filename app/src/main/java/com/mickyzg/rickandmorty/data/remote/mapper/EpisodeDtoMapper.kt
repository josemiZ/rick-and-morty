package com.mickyzg.rickandmorty.data.remote.mapper

import com.mickyzg.rickandmorty.data.remote.dto.EpisodeDto
import com.mickyzg.rickandmorty.domain.model.Episode
import javax.inject.Inject

/**
 * Maps [EpisodeDto] (remote) to [Episode] (domain).
 *
 * Provides two mapping strategies:
 *  - [mapListItem]: populates only list-level fields; detail fields remain `null`
 *    and [Episode.isDetailLoaded] is `false`. Used when persisting a page of results.
 *  - [map]: populates all fields including detail fields; sets [Episode.isDetailLoaded]
 *    to `true`. Used when persisting a single episode detail response.
 *
 * Injectable via Hilt; easy to mock in unit tests.
 */
class EpisodeDtoMapper @Inject constructor() {

    /**
     * Maps an [EpisodeDto] to an [Episode] with only list-level fields populated.
     * Detail fields ([Episode.characterUrls], [Episode.url], etc.) are left `null`.
     */
    fun mapListItem(dto: EpisodeDto): Episode = Episode(
        id = dto.id,
        name = dto.name,
        airDate = dto.airDate,
        episodeCode = dto.episodeCode,
        isDetailLoaded = false
    )

    /**
     * Maps an [EpisodeDto] to a fully populated [Episode] with all detail fields.
     * Sets [Episode.isDetailLoaded] to `true`.
     */
    fun map(dto: EpisodeDto): Episode = Episode(
        id = dto.id,
        name = dto.name,
        airDate = dto.airDate,
        episodeCode = dto.episodeCode,
        characterUrls = dto.characters,
        url = dto.url,
        createdAtIso = dto.created,
        isDetailLoaded = true
    )

    /**
     * Maps a list of [EpisodeDto] using [mapListItem] for efficient list rendering.
     */
    fun mapList(dtos: List<EpisodeDto>): List<Episode> = dtos.map(::mapListItem)
}

