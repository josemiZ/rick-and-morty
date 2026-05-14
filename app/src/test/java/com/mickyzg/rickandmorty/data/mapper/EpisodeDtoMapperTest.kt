package com.mickyzg.rickandmorty.data.mapper

import com.mickyzg.rickandmorty.data.remote.dto.EpisodeDto
import com.mickyzg.rickandmorty.data.remote.mapper.EpisodeDtoMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EpisodeDtoMapperTest {

    private lateinit var mapper: EpisodeDtoMapper

    @Before
    fun setUp() {
        mapper = EpisodeDtoMapper()
    }

    // ── mapListItem ───────────────────────────────────────────────────────────

    @Test
    fun `mapListItem maps required list fields correctly`() {
        val dto = buildDto(id = 3, name = "Pickle Rick", episodeCode = "S03E03")
        val result = mapper.mapListItem(dto)
        assertEquals(3, result.id)
        assertEquals("Pickle Rick", result.name)
        assertEquals("December 2, 2013", result.airDate)
        assertEquals("S03E03", result.episodeCode)
    }

    @Test
    fun `mapListItem sets isDetailLoaded to false`() {
        assertFalse(mapper.mapListItem(buildDto()).isDetailLoaded)
    }

    @Test
    fun `mapListItem leaves detail fields null`() {
        val result = mapper.mapListItem(buildDto())
        assertNull(result.characterUrls)
        assertNull(result.url)
        assertNull(result.createdAtIso)
    }

    // ── map (full detail) ─────────────────────────────────────────────────────

    @Test
    fun `map sets isDetailLoaded to true`() {
        assertTrue(mapper.map(buildDto()).isDetailLoaded)
    }

    @Test
    fun `map sets character urls from dto`() {
        val chars = listOf("https://char/1", "https://char/2")
        val result = mapper.map(buildDto(characters = chars))
        assertEquals(chars, result.characterUrls)
    }

    @Test
    fun `map sets url and createdAtIso`() {
        val result = mapper.map(buildDto(url = "https://ep/1", created = "2017-11-10T12:56:33.798Z"))
        assertEquals("https://ep/1", result.url)
        assertEquals("2017-11-10T12:56:33.798Z", result.createdAtIso)
    }

    @Test
    fun `map preserves all list fields`() {
        val dto = buildDto(id = 7, name = "Pilot", airDate = "December 2, 2013", episodeCode = "S01E01")
        val result = mapper.map(dto)
        assertEquals(7, result.id)
        assertEquals("Pilot", result.name)
        assertEquals("December 2, 2013", result.airDate)
        assertEquals("S01E01", result.episodeCode)
    }

    // ── mapList ───────────────────────────────────────────────────────────────

    @Test
    fun `mapList returns same count as input`() {
        val dtos = listOf(buildDto(id = 1), buildDto(id = 2), buildDto(id = 3))
        assertEquals(3, mapper.mapList(dtos).size)
    }

    @Test
    fun `mapList sets isDetailLoaded to false for every item`() {
        val results = mapper.mapList(listOf(buildDto(id = 1), buildDto(id = 2)))
        results.forEach { assertFalse(it.isDetailLoaded) }
    }

    @Test
    fun `mapList returns empty list for empty input`() {
        assertEquals(0, mapper.mapList(emptyList()).size)
    }

    // ── Fixture ───────────────────────────────────────────────────────────────

    private fun buildDto(
        id: Int = 1,
        name: String = "Pilot",
        airDate: String = "December 2, 2013",
        episodeCode: String = "S01E01",
        characters: List<String> = listOf("https://char/1"),
        url: String = "https://rickandmortyapi.com/api/episode/1",
        created: String = "2017-11-10T12:56:33.798Z"
    ) = EpisodeDto(
        id = id,
        name = name,
        airDate = airDate,
        episodeCode = episodeCode,
        characters = characters,
        url = url,
        created = created
    )
}

