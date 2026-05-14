package com.mickyzg.rickandmorty.data.mapper

import com.mickyzg.rickandmorty.data.remote.dto.CharacterDto
import com.mickyzg.rickandmorty.data.remote.dto.CharacterLocationDto
import com.mickyzg.rickandmorty.data.remote.mapper.CharacterDtoMapper
import com.mickyzg.rickandmorty.domain.model.CharacterGender
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CharacterDtoMapperTest {

    private lateinit var mapper: CharacterDtoMapper

    @Before
    fun setUp() {
        mapper = CharacterDtoMapper()
    }

    // ── mapListItem ───────────────────────────────────────────────────────────

    @Test
    fun `mapListItem maps required list fields correctly`() {
        val dto = buildDto(id = 42, name = "Morty Smith", status = "Alive", species = "Human", image = "https://img/42")
        val result = mapper.mapListItem(dto)
        assertEquals(42, result.id)
        assertEquals("Morty Smith", result.name)
        assertEquals(CharacterStatus.ALIVE, result.status)
        assertEquals("Human", result.species)
        assertEquals("https://img/42", result.imageUrl)
    }

    @Test
    fun `mapListItem sets isDetailLoaded to false`() {
        val result = mapper.mapListItem(buildDto())
        assertFalse(result.isDetailLoaded)
    }

    @Test
    fun `mapListItem leaves detail fields null`() {
        val result = mapper.mapListItem(buildDto())
        assertNull(result.gender)
        assertNull(result.origin)
        assertNull(result.episodeUrls)
    }

    // ── map (full detail) ─────────────────────────────────────────────────────

    @Test
    fun `map sets isDetailLoaded to true`() {
        val result = mapper.map(buildDto())
        assertTrue(result.isDetailLoaded)
    }

    @Test
    fun `map converts blank type to null`() {
        val result = mapper.map(buildDto(type = ""))
        assertNull(result.type)
    }

    @Test
    fun `map preserves non-blank type`() {
        val result = mapper.map(buildDto(type = "Cronenberg"))
        assertEquals("Cronenberg", result.type)
    }

    @Test
    fun `map converts gender string to enum`() {
        assertEquals(CharacterGender.MALE, mapper.map(buildDto(gender = "Male")).gender)
        assertEquals(CharacterGender.FEMALE, mapper.map(buildDto(gender = "Female")).gender)
        assertEquals(CharacterGender.GENDERLESS, mapper.map(buildDto(gender = "Genderless")).gender)
        assertEquals(CharacterGender.UNKNOWN, mapper.map(buildDto(gender = "n/a")).gender)
    }

    @Test
    fun `map sets episode urls from dto`() {
        val episodes = listOf("https://ep/1", "https://ep/2")
        val result = mapper.map(buildDto(episode = episodes))
        assertEquals(episodes, result.episodeUrls)
    }

    // ── mapList ───────────────────────────────────────────────────────────────

    @Test
    fun `mapList returns same count as input`() {
        val dtos = listOf(buildDto(id = 1), buildDto(id = 2), buildDto(id = 3))
        assertEquals(3, mapper.mapList(dtos).size)
    }

    @Test
    fun `unknown status string maps to UNKNOWN`() {
        val result = mapper.mapListItem(buildDto(status = "zombie"))
        assertEquals(CharacterStatus.UNKNOWN, result.status)
    }

    // ── Fixture ───────────────────────────────────────────────────────────────

    private fun buildDto(
        id: Int = 1,
        name: String = "Rick Sanchez",
        status: String = "Alive",
        species: String = "Human",
        type: String = "",
        gender: String = "Male",
        image: String = "https://img/1.jpg",
        episode: List<String> = listOf("https://ep/1")
    ) = CharacterDto(
        id = id, name = name, status = status, species = species, type = type,
        gender = gender,
        origin = CharacterLocationDto("Earth (C-137)", "https://location/1"),
        location = CharacterLocationDto("Citadel of Ricks", "https://location/3"),
        image = image, episode = episode,
        url = "https://char/$id",
        created = "2017-11-04T18:48:46.250Z"
    )
}

