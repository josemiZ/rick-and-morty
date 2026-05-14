package com.mickyzg.rickandmorty.data.local.mapper

import com.mickyzg.rickandmorty.data.local.LocationEntity
import com.mickyzg.rickandmorty.domain.model.Location
import javax.inject.Inject

private const val URL_SEPARATOR = ","

/**
 * Bidirectional mapper between [LocationEntity] (local) and [Location] (domain).
 *
 * Handles serialization of [List<String>] (resident URLs) to/from a comma-separated
 * string for Room storage.
 *
 * Injectable via Hilt; easy to mock in unit tests.
 */
class LocationEntityMapper @Inject constructor() {

    /**
     * Converts a [LocationEntity] from the local database to a [Location] domain model.
     */
    fun toDomain(entity: LocationEntity): Location = Location(
        id = entity.id,
        name = entity.name,
        type = entity.type,
        dimension = entity.dimension,
        residentUrls = entity.residentUrlsJson
            ?.split(URL_SEPARATOR)
            ?.filter { it.isNotBlank() },
        url = entity.url,
        createdAtIso = entity.createdAtIso,
        isDetailLoaded = entity.isDetailLoaded
    )

    /**
     * Converts a [Location] domain model to a [LocationEntity] for local storage.
     *
     * @param domain the location domain model to persist.
     * @param pageIndex the 0-based page position used to preserve list ordering.
     */
    fun toEntity(domain: Location, pageIndex: Int): LocationEntity = LocationEntity(
        id = domain.id,
        name = domain.name,
        type = domain.type,
        dimension = domain.dimension,
        pageIndex = pageIndex,
        residentUrlsJson = domain.residentUrls?.joinToString(URL_SEPARATOR),
        url = domain.url,
        createdAtIso = domain.createdAtIso,
        isDetailLoaded = domain.isDetailLoaded
    )
}

