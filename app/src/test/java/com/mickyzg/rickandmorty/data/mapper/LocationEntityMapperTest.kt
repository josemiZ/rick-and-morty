package com.mickyzg.rickandmorty.data.mapper

import com.mickyzg.rickandmorty.data.local.LocationEntity
import com.mickyzg.rickandmorty.data.local.mapper.LocationEntityMapper
import com.mickyzg.rickandmorty.domain.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationEntityMapperTest {

    private lateinit var mapper: LocationEntityMapper

    @Before
    fun setUp() {
        mapper = LocationEntityMapper()
    }

    // -- toDomain -------------------------------------------------------------

    @Test
    fun toDomain_mapsBaseListFieldsCorrectly() {
        val entity = buildEntity(id = 4, name = "Citadel of Ricks", type = "Space station", dimension = "unknown")
        val result = mapper.toDomain(entity)
        assertEquals(4, result.id)
        assertEquals("Citadel of Ricks", result.name)
        assertEquals("Space station", result.type)
        assertEquals("unknown", result.dimension)
    }

    @Test
    fun toDomain_splitsResidentUrlsJsonIntoList() {
        val urls = listOf("https://char/1", "https://char/2", "https://char/3")
        val result = mapper.toDomain(buildEntity(residentUrlsJson = urls.joinToString(",")))
        assertEquals(urls, result.residentUrls)
    }

    @Test
    fun toDomain_returnsNullResidentUrlsWhenJsonIsNull() {
        assertNull(mapper.toDomain(buildEntity(residentUrlsJson = null)).residentUrls)
    }

    @Test
    fun toDomain_filtersBlankEntriesFromResidentUrlsJson() {
        val result = mapper.toDomain(buildEntity(residentUrlsJson = "https://char/1,,https://char/2"))
        assertEquals(listOf("https://char/1", "https://char/2"), result.residentUrls)
    }

    @Test
    fun toDomain_mapsIsDetailLoadedCorrectly() {
        assertFalse(mapper.toDomain(buildEntity(isDetailLoaded = false)).isDetailLoaded)
        assertTrue(mapper.toDomain(buildEntity(isDetailLoaded = true)).isDetailLoaded)
    }

    @Test
    fun toDomain_mapsDetailFieldsWhenPresent() {
        val entity = buildEntity(url = "https://loc/1", createdAtIso = "2017-11-10T12:42:04.162Z")
        val result = mapper.toDomain(entity)
        assertEquals("https://loc/1", result.url)
        assertEquals("2017-11-10T12:42:04.162Z", result.createdAtIso)
    }

    // -- toEntity -------------------------------------------------------------

    @Test
    fun toEntity_storesPageIndexCorrectly() {
        assertEquals(6, mapper.toEntity(buildLocation(), pageIndex = 6).pageIndex)
    }

    @Test
    fun toEntity_serializesResidentUrlsAsCommaSeparatedString() {
        val location = buildLocation(residentUrls = listOf("a", "b", "c"))
        assertEquals("a,b,c", mapper.toEntity(location, pageIndex = 0).residentUrlsJson)
    }

    @Test
    fun toEntity_storesNullResidentUrlsJsonWhenUrlsAreNull() {
        assertNull(mapper.toEntity(buildLocation(residentUrls = null), pageIndex = 0).residentUrlsJson)
    }

    @Test
    fun toEntity_preservesIsDetailLoaded() {
        assertTrue(mapper.toEntity(buildLocation(isDetailLoaded = true), 0).isDetailLoaded)
        assertFalse(mapper.toEntity(buildLocation(isDetailLoaded = false), 0).isDetailLoaded)
    }

    // -- round-trip -----------------------------------------------------------

    @Test
    fun roundTrip_preservesAllFields() {
        val original = buildLocation(
            id = 42,
            name = "Anatomy Park",
            type = "Anatomy Park",
            dimension = "Dimension C-137",
            residentUrls = listOf("https://char/1", "https://char/2"),
            isDetailLoaded = true
        )
        val restored = mapper.toDomain(mapper.toEntity(original, pageIndex = 5))
        assertEquals(original.id, restored.id)
        assertEquals(original.name, restored.name)
        assertEquals(original.type, restored.type)
        assertEquals(original.dimension, restored.dimension)
        assertEquals(original.residentUrls, restored.residentUrls)
        assertTrue(restored.isDetailLoaded)
    }

    // -- Fixtures -------------------------------------------------------------

    private fun buildEntity(
        id: Int = 1,
        name: String = "Earth (C-137)",
        type: String = "Planet",
        dimension: String = "Dimension C-137",
        residentUrlsJson: String? = null,
        url: String? = null,
        createdAtIso: String? = null,
        isDetailLoaded: Boolean = false
    ) = LocationEntity(
        id = id, name = name, type = type, dimension = dimension,
        residentUrlsJson = residentUrlsJson, url = url,
        createdAtIso = createdAtIso, isDetailLoaded = isDetailLoaded
    )

    private fun buildLocation(
        id: Int = 1,
        name: String = "Earth (C-137)",
        type: String = "Planet",
        dimension: String = "Dimension C-137",
        residentUrls: List<String>? = null,
        isDetailLoaded: Boolean = false
    ) = Location(
        id = id, name = name, type = type, dimension = dimension,
        residentUrls = residentUrls, isDetailLoaded = isDetailLoaded
    )
}
