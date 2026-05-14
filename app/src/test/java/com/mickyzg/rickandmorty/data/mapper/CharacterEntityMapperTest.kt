package com.mickyzg.rickandmorty.data.mapper

import com.mickyzg.rickandmorty.data.local.CharacterEntity
import com.mickyzg.rickandmorty.data.local.mapper.CharacterEntityMapper
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterGender
import com.mickyzg.rickandmorty.domain.model.CharacterLocation
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CharacterEntityMapperTest {

    private lateinit var mapper: CharacterEntityMapper

    @Before
    fun setUp() {
        mapper = CharacterEntityMapper()
    }

    // ── toDomain ──────────────────────────────────────────────────────────────

    @Test
    fun `toDomain maps base list fields correctly`() {
        val entity = buildEntity(id = 7, name = "Beth Smith", status = "Alive", isFavorite = true)
        val result = mapper.toDomain(entity)
        assertEquals(7, result.id)
        assertEquals("Beth Smith", result.name)
        assertEquals(CharacterStatus.ALIVE, result.status)
        assertTrue(result.isFavorite)
    }

    @Test
    fun `toDomain splits episodeUrlsJson into list`() {
        val urls = listOf("ep1", "ep2", "ep3")
        val entity = buildEntity(episodeUrlsJson = urls.joinToString(","))
        val result = mapper.toDomain(entity)
        assertEquals(urls, result.episodeUrls)
    }

    @Test
    fun `toDomain returns null episodeUrls when json is null`() {
        val entity = buildEntity(episodeUrlsJson = null)
        val result = mapper.toDomain(entity)
        assertNull(result.episodeUrls)
    }

    @Test
    fun `toDomain builds CharacterLocation from originName and originUrl`() {
        val entity = buildEntity(originName = "Earth", originUrl = "https://loc/1")
        val result = mapper.toDomain(entity)
        assertEquals("Earth", result.origin?.name)
        assertEquals("https://loc/1", result.origin?.url)
    }

    @Test
    fun `toDomain returns null origin when originName is null`() {
        val entity = buildEntity(originName = null, originUrl = null)
        val result = mapper.toDomain(entity)
        assertNull(result.origin)
    }

    // ── toEntity ──────────────────────────────────────────────────────────────

    @Test
    fun `toEntity preserves explicit isFavorite override`() {
        val character = buildCharacter(isFavorite = false)
        val entity = mapper.toEntity(character, pageIndex = 5, isFavorite = true)
        assertTrue(entity.isFavorite)
        assertEquals(5, entity.pageIndex)
    }

    @Test
    fun `toEntity uses domain isFavorite when not overridden`() {
        val character = buildCharacter(isFavorite = true)
        val entity = mapper.toEntity(character, pageIndex = 0)
        assertTrue(entity.isFavorite)
    }

    @Test
    fun `toEntity serializes episode urls as comma-separated string`() {
        val character = buildCharacter(episodeUrls = listOf("a", "b", "c"))
        val entity = mapper.toEntity(character, pageIndex = 0)
        assertEquals("a,b,c", entity.episodeUrlsJson)
    }

    @Test
    fun `toEntity round-trip preserves all set fields`() {
        val original = buildCharacter(
            id = 99,
            name = "Summer",
            isFavorite = true,
            episodeUrls = listOf("ep1", "ep2"),
            isDetailLoaded = true
        )
        val entity = mapper.toEntity(original, pageIndex = 3)
        val restored = mapper.toDomain(entity)
        assertEquals(original.id, restored.id)
        assertEquals(original.name, restored.name)
        assertEquals(original.isFavorite, restored.isFavorite)
        assertEquals(original.episodeUrls, restored.episodeUrls)
        assertTrue(restored.isDetailLoaded)
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildEntity(
        id: Int = 1,
        name: String = "Rick",
        status: String = "Alive",
        isFavorite: Boolean = false,
        episodeUrlsJson: String? = null,
        originName: String? = null,
        originUrl: String? = null
    ) = CharacterEntity(
        id = id, name = name, status = status, species = "Human",
        imageUrl = "https://img/$id", isFavorite = isFavorite,
        episodeUrlsJson = episodeUrlsJson,
        originName = originName, originUrl = originUrl
    )

    private fun buildCharacter(
        id: Int = 1,
        name: String = "Rick",
        isFavorite: Boolean = false,
        episodeUrls: List<String>? = null,
        isDetailLoaded: Boolean = false
    ) = Character(
        id = id, name = name,
        status = CharacterStatus.ALIVE, species = "Human",
        imageUrl = "https://img/$id", isFavorite = isFavorite,
        gender = CharacterGender.MALE,
        origin = CharacterLocation("Earth", "https://loc/1"),
        location = CharacterLocation("Citadel", "https://loc/3"),
        episodeUrls = episodeUrls,
        isDetailLoaded = isDetailLoaded
    )
}

