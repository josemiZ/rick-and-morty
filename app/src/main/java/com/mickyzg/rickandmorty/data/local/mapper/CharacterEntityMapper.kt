package com.mickyzg.rickandmorty.data.local.mapper

import com.mickyzg.rickandmorty.data.local.CharacterEntity
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterGender
import com.mickyzg.rickandmorty.domain.model.CharacterLocation
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import javax.inject.Inject

private const val URL_SEPARATOR = ","

/**
 * Bidirectional mapper between [CharacterEntity] (local) and [Character] (domain).
 *
 * Handles serialization of [List<String>] (episode URLs) to/from a comma-separated
 * string for Room storage, since Room does not natively support list columns.
 *
 * Injectable via Hilt; easy to mock in unit tests.
 */
class CharacterEntityMapper @Inject constructor() {

    /**
     * Converts a [CharacterEntity] from the local database to a [Character] domain model.
     * The [Character.isFavorite] flag is always read from the entity (user-controlled).
     */
    fun toDomain(entity: CharacterEntity): Character = Character(
        id = entity.id,
        name = entity.name,
        status = CharacterStatus.fromRaw(entity.status),
        species = entity.species,
        imageUrl = entity.imageUrl,
        isFavorite = entity.isFavorite,
        type = entity.type,
        gender = entity.gender?.let { CharacterGender.fromRaw(it) },
        origin = if (entity.originName != null) {
            CharacterLocation(
                name = entity.originName,
                url = entity.originUrl.orEmpty()
            )
        } else null,
        location = if (entity.locationName != null) {
            CharacterLocation(
                name = entity.locationName,
                url = entity.locationUrl.orEmpty()
            )
        } else null,
        episodeUrls = entity.episodeUrlsJson?.split(URL_SEPARATOR)?.filter { it.isNotBlank() },
        createdAtIso = entity.createdAtIso,
        isDetailLoaded = entity.isDetailLoaded
    )

    /**
     * Converts a [Character] domain model to a [CharacterEntity] for local storage.
     *
     * @param domain the character domain model to persist.
     * @param pageIndex the 0-based page position used to preserve list ordering.
     * @param isFavorite explicit favorite state; defaults to the domain model value so
     * that a detail refresh does not accidentally overwrite the user's preference.
     */
    fun toEntity(
        domain: Character,
        pageIndex: Int,
        isFavorite: Boolean = domain.isFavorite
    ): CharacterEntity = CharacterEntity(
        id = domain.id,
        name = domain.name,
        status = domain.status.name,
        species = domain.species,
        imageUrl = domain.imageUrl,
        pageIndex = pageIndex,
        isFavorite = isFavorite,
        type = domain.type,
        gender = domain.gender?.name,
        originName = domain.origin?.name,
        originUrl = domain.origin?.url,
        locationName = domain.location?.name,
        locationUrl = domain.location?.url,
        episodeUrlsJson = domain.episodeUrls?.joinToString(URL_SEPARATOR),
        createdAtIso = domain.createdAtIso,
        isDetailLoaded = domain.isDetailLoaded
    )
}

