package com.mickyzg.rickandmorty.data.local.mapper

import com.mickyzg.rickandmorty.data.local.EpisodeEntity
import com.mickyzg.rickandmorty.domain.model.Episode
import javax.inject.Inject

private const val URL_SEPARATOR = ","

/**
 * Bidirectional mapper between [EpisodeEntity] (local) and [Episode] (domain).
 *
 * Handles serialization of [List<String>] (character URLs) to/from a comma-separated
 * string for Room storage.
 *
 * Injectable via Hilt; easy to mock in unit tests.
 */
class EpisodeEntityMapper @Inject constructor() {

    /**
     * Converts an [EpisodeEntity] from the local database to an [Episode] domain model.
     */
    fun toDomain(entity: EpisodeEntity): Episode = Episode(
        id = entity.id,
        name = entity.name,
        airDate = entity.airDate,
        episodeCode = entity.episodeCode,
        characterUrls = entity.characterUrlsJson
            ?.split(URL_SEPARATOR)
            ?.filter { it.isNotBlank() },
        url = entity.url,
        createdAtIso = entity.createdAtIso,
        isDetailLoaded = entity.isDetailLoaded
    )

    /**
     * Converts an [Episode] domain model to an [EpisodeEntity] for local storage.
     *
     * @param domain the episode domain model to persist.
     * @param pageIndex the 0-based page position used to preserve list ordering.
     */
    fun toEntity(domain: Episode, pageIndex: Int): EpisodeEntity = EpisodeEntity(
        id = domain.id,
        name = domain.name,
        airDate = domain.airDate,
        episodeCode = domain.episodeCode,
        pageIndex = pageIndex,
        characterUrlsJson = domain.characterUrls?.joinToString(URL_SEPARATOR),
        url = domain.url,
        createdAtIso = domain.createdAtIso,
        isDetailLoaded = domain.isDetailLoaded
    )
}

