package com.mickyzg.rickandmorty.data.remote.mapper

import com.mickyzg.rickandmorty.data.remote.dto.CharacterDto
import com.mickyzg.rickandmorty.data.remote.dto.CharacterLocationDto
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterGender
import com.mickyzg.rickandmorty.domain.model.CharacterLocation
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import javax.inject.Inject

/**
 * Maps [CharacterDto] (remote) to [Character] (domain).
 *
 * Provides two mapping strategies:
 *  - [mapListItem]: populates only list-level fields; detail fields remain `null`
 *    and [Character.isDetailLoaded] is `false`. Used when persisting a page of results.
 *  - [map]: populates all fields including detail fields; sets [Character.isDetailLoaded]
 *    to `true`. Used when persisting a single character detail response.
 *
 * Injectable via Hilt; easy to mock in unit tests.
 */
class CharacterDtoMapper @Inject constructor() {

    /**
     * Maps a [CharacterDto] to a [Character] with only list-level fields populated.
     * Detail fields ([Character.type], [Character.gender], etc.) are left `null`.
     */
    fun mapListItem(dto: CharacterDto): Character = Character(
        id = dto.id,
        name = dto.name,
        status = CharacterStatus.fromRaw(dto.status),
        species = dto.species,
        imageUrl = dto.image,
        isDetailLoaded = false
    )

    /**
     * Maps a [CharacterDto] to a fully populated [Character] with all detail fields.
     * Sets [Character.isDetailLoaded] to `true`.
     */
    fun map(dto: CharacterDto): Character = Character(
        id = dto.id,
        name = dto.name,
        status = CharacterStatus.fromRaw(dto.status),
        species = dto.species,
        imageUrl = dto.image,
        type = dto.type.ifBlank { null },
        gender = CharacterGender.fromRaw(dto.gender),
        origin = mapLocation(dto.origin),
        location = mapLocation(dto.location),
        episodeUrls = dto.episode,
        createdAtIso = dto.created,
        isDetailLoaded = true
    )

    /**
     * Maps a list of [CharacterDto] using [mapListItem] for efficient list rendering.
     */
    fun mapList(dtos: List<CharacterDto>): List<Character> = dtos.map(::mapListItem)

    private fun mapLocation(dto: CharacterLocationDto): CharacterLocation = CharacterLocation(
        name = dto.name,
        url = dto.url
    )
}

