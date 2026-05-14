package com.mickyzg.rickandmorty.data.mapper

import com.mickyzg.rickandmorty.data.remote.dto.LocationDto
import com.mickyzg.rickandmorty.data.remote.mapper.LocationDtoMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationDtoMapperTest {

    private lateinit var mapper: LocationDtoMapper

    @Before
    fun setUp() {
        mapper = LocationDtoMapper()
    }

    // -- mapListItem ----------------------------------------------------------

    @Test
    fun mapListItem_mapsRequiredListFieldsCorrectly() {
        val dto = buildDto(id = 2, name = "Citadel of Ricks", type = "Space station", dimension = "unknown")
        val result = mapper.mapListItem(dto)
        assertEquals(2, result.id)
        assertEquals("Citadel of Ricks", result.name)
        assertEquals("Space station", result.type)
        assertEquals("unknown", result.dimension)
    }

    @Test
    fun mapListItem_setsIsDetailLoadedToFalse() {
        assertFalse(mapper.mapListItem(buildDto()).isDetailLoaded)
    }

    @Test
    fun mapListItem_leavesDetailFieldsNull() {
        val result = mapper.mapListItem(buildDto())
        assertNull(result.residentUrls)
        assertNull(result.url)
        assertNull(result.createdAtIso)
    }

    @Test
    fun mapListItem_convertsBlankTypeToEmptyString() {
        assertEquals("", mapper.mapListItem(buildDto(type = "")).type)
    }

    @Test
    fun mapListItem_convertsBlankDimensionToEmptyString() {
        assertEquals("", mapper.mapListItem(buildDto(dimension = "")).dimension)
    }

    // -- map (full detail) ----------------------------------------------------

    @Test
    fun map_setsIsDetailLoadedToTrue() {
        assertTrue(mapper.map(buildDto()).isDetailLoaded)
    }

    @Test
    fun map_setsResidentUrlsFromDto() {
        val residents = listOf("https://char/1", "https://char/2")
        assertEquals(residents, mapper.map(buildDto(residents = residents)).residentUrls)
    }

    @Test
    fun map_setsUrlAndCreatedAtIso() {
        val result = mapper.map(buildDto(url = "https://loc/1", created = "2017-11-10T12:42:04.162Z"))
        assertEquals("https://loc/1", result.url)
        assertEquals("2017-11-10T12:42:04.162Z", result.createdAtIso)
    }

    @Test
    fun map_convertsBlankTypeToEmptyString() {
        assertEquals("", mapper.map(buildDto(type = "")).type)
    }

    @Test
    fun map_preservesAllListFields() {
        val dto = buildDto(id = 3, name = "Earth (C-137)", type = "Planet", dimension = "Dimension C-137")
        val result = mapper.map(dto)
        assertEquals(3, result.id)
        assertEquals("Earth (C-137)", result.name)
        assertEquals("Planet", result.type)
        assertEquals("Dimension C-137", result.dimension)
    }

    // -- mapList --------------------------------------------------------------

    @Test
    fun mapList_returnsSameCountAsInput() {
        assertEquals(3, mapper.mapList(listOf(buildDto(1), buildDto(2), buildDto(3))).size)
    }

    @Test
    fun mapList_setsIsDetailLoadedToFalseForEveryItem() {
        mapper.mapList(listOf(buildDto(1), buildDto(2))).forEach { assertFalse(it.isDetailLoaded) }
    }

    @Test
    fun mapList_returnsEmptyListForEmptyInput() {
        assertEquals(0, mapper.mapList(emptyList()).size)
    }

    // -- Fixture --------------------------------------------------------------

    private fun buildDto(
        id: Int = 1,
        name: String = "Earth (C-137)",
        type: String = "Planet",
        dimension: String = "Dimension C-137",
        residents: List<String> = listOf("https://char/1"),
        url: String = "https://rickandmortyapi.com/api/location/1",
        created: String = "2017-11-10T12:42:04.162Z"
    ) = LocationDto(
        id = id, name = name, type = type, dimension = dimension,
        residents = residents, url = url, created = created
    )
}
