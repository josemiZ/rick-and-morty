package com.mickyzg.rickandmorty.data.mapper

import com.mickyzg.rickandmorty.data.local.EpisodeEntity
import com.mickyzg.rickandmorty.data.local.mapper.EpisodeEntityMapper
import com.mickyzg.rickandmorty.domain.model.Episode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EpisodeEntityMapperTest {

    private lateinit var mapper: EpisodeEntityMapper

    @Before
    fun setUp() {
        mapper = EpisodeEntityMapper()
    }

    // ── toDomain ──────────────────────────────────────────────────────────────

    @Test
    fun `toDomain maps base list fields correctly`() {
        val entity = buildEntity(id = 5, name = "Pilot", episodeCode = "S01E01")
        val result = mapper.toDomain(entity)
        assertEquals(5, result.id)
        assertEquals("Pilot", result.name)
        assertEquals("December 2, 2013", result.airDate)
        assertEquals("S01E01", result.episodeCode)
    }

    @Test
    fun `toDomain splits characterUrlsJson into list`() {
        val urls = listOf("https://char/1", "https://char/2", "https://char/3")
        val result = mapper.toDomain(buildEntity(characterUrlsJson = urls.joinToString(",")))
        assertEquals(urls, result.characterUrls)
    }

    @Test
    fun `toDomain returns null characterUrls when json is null`() {
        val result = mapper.toDomain(buildEntity(characterUrlsJson = null))
        assertNull(result.characterUrls)
    }

    @Test
    fun `toDomain filters blank entries from characterUrlsJson`() {
        val result = mapper.toDomain(buildEntity(characterUrlsJson = "https://char/1,,https://char/2"))
        assertEquals(listOf("https://char/1", "https://char/2"), result.characterUrls)
    }

    @Test
    fun `toDomain maps isDetailLoaded correctly`() {
        assertFalse(mapper.toDomain(buildEntity(isDetailLoaded = false)).isDetailLoaded)
        assertTrue(mapper.toDomain(buildEntity(isDetailLoaded = true)).isDetailLoaded)
    }

    @Test
    fun `toDomain maps detail fields when present`() {
        val entity = buildEntity(url = "https://ep/1", createdAtIso = "2017-11-10T12:56:33.798Z")
        val result = mapper.toDomain(entity)
        assertEquals("https://ep/1", result.url)
        assertEquals("2017-11-10T12:56:33.798Z", result.createdAtIso)
    }

    // ── toEntity ──────────────────────────────────────────────────────────────

    @Test
    fun `toEntity stores pageIndex correctly`() {
        val entity = mapper.toEntity(buildEpisode(), pageIndex = 9)
        assertEquals(9, entity.pageIndex)
    }

    @Test
    fun `toEntity serializes characterUrls as comma-separated string`() {
        val episode = buildEpisode(characterUrls = listOf("a", "b", "c"))
        assertEquals("a,b,c", mapper.toEntity(episode, pageIndex = 0).characterUrlsJson)
    }

    @Test
    fun `toEntity stores null characterUrlsJson when urls are null`() {
        val episode = buildEpisode(characterUrls = null)
        assertNull(mapper.toEntity(episode, pageIndex = 0).characterUrlsJson)
    }

    @Test
    fun `toEntity preserves isDetailLoaded`() {
        assertTrue(mapper.toEntity(buildEpisode(isDetailLoaded = true), 0).isDetailLoaded)
        assertFalse(mapper.toEntity(buildEpisode(isDetailLoaded = false), 0).isDetailLoaded)
    }

    // ── round-trip ────────────────────────────────────────────────────────────

    @Test
    fun `toEntity then toDomain round-trip preserves all fields`() {
        val original = buildEpisode(
            id = 42,
            name = "Pickle Rick",
            episodeCode = "S03E03",
            characterUrls = listOf("https://char/1", "https://char/2"),
            isDetailLoaded = true
        )
        val restored = mapper.toDomain(mapper.toEntity(original, pageIndex = 7))
        assertEquals(original.id, restored.id)
        assertEquals(original.name, restored.name)
        assertEquals(original.airDate, restored.airDate)
        assertEquals(original.episodeCode, restored.episodeCode)
        assertEquals(original.characterUrls, restored.characterUrls)
        assertTrue(restored.isDetailLoaded)
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildEntity(
        id: Int = 1,
        name: String = "Pilot",
        episodeCode: String = "S01E01",
        characterUrlsJson: String? = null,
        url: String? = null,
        createdAtIso: String? = null,
        isDetailLoaded: Boolean = false
    ) = EpisodeEntity(
        id = id,
        name = name,
        airDate = "December 2, 2013",
        episodeCode = episodeCode,
        characterUrlsJson = characterUrlsJson,
        url = url,
        createdAtIso = createdAtIso,
        isDetailLoaded = isDetailLoaded
    )

    private fun buildEpisode(
        id: Int = 1,
        name: String = "Pilot",
        episodeCode: String = "S01E01",
        characterUrls: List<String>? = null,
        isDetailLoaded: Boolean = false
    ) = Episode(
        id = id,
        name = name,
        airDate = "December 2, 2013",
        episodeCode = episodeCode,
        characterUrls = characterUrls,
        isDetailLoaded = isDetailLoaded
    )
}

