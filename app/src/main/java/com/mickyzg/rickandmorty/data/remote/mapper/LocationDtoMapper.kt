package com.mickyzg.rickandmorty.data.remote.mapper

import com.mickyzg.rickandmorty.data.remote.dto.LocationDto
import com.mickyzg.rickandmorty.domain.model.Location
import javax.inject.Inject

/**
 * Maps [LocationDto] (remote) to [Location] (domain).
 *
 * Provides two mapping strategies:
 *  - [mapListItem]: populates only list-level fields; detail fields remain `null`
 *    and [Location.isDetailLoaded] is `false`. Used when persisting a page of results.
 *  - [map]: populates all fields including detail fields; sets [Location.isDetailLoaded]
 *    to `true`. Used when persisting a single location detail response.
 *
 * Injectable via Hilt; easy to mock in unit tests.
 */
class LocationDtoMapper @Inject constructor() {

    /**
     * Maps a [LocationDto] to a [Location] with only list-level fields populated.
     * Detail fields ([Location.residentUrls], [Location.url], etc.) are left `null`.
     */
    fun mapListItem(dto: LocationDto): Location = Location(
        id = dto.id,
        name = dto.name,
        type = dto.type.ifBlank { "" },
        dimension = dto.dimension.ifBlank { "" },
        isDetailLoaded = false
    )

    /**
     * Maps a [LocationDto] to a fully populated [Location] with all detail fields.
     * Sets [Location.isDetailLoaded] to `true`.
     */
    fun map(dto: LocationDto): Location = Location(
        id = dto.id,
        name = dto.name,
        type = dto.type.ifBlank { "" },
        dimension = dto.dimension.ifBlank { "" },
        residentUrls = dto.residents,
        url = dto.url,
        createdAtIso = dto.created,
        isDetailLoaded = true
    )

    /**
     * Maps a list of [LocationDto] using [mapListItem] for efficient list rendering.
     */
    fun mapList(dtos: List<LocationDto>): List<Location> = dtos.map(::mapListItem)
}

