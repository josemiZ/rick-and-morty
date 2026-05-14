package com.mickyzg.rickandmorty.data.repository

import com.mickyzg.rickandmorty.data.local.LocationDao
import com.mickyzg.rickandmorty.data.local.LocationEntity
import com.mickyzg.rickandmorty.data.local.mapper.LocationEntityMapper
import com.mickyzg.rickandmorty.data.remote.RickAndMortyService
import com.mickyzg.rickandmorty.data.remote.dto.ApiResponse
import com.mickyzg.rickandmorty.data.remote.dto.LocationDto
import com.mickyzg.rickandmorty.data.remote.dto.PageInfoDto
import com.mickyzg.rickandmorty.data.remote.mapper.LocationDtoMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class LocationRepositoryImplTest {

    private val dao: LocationDao = mockk(relaxed = true)
    private val service: RickAndMortyService = mockk()
    private val dtoMapper = LocationDtoMapper()
    private val entityMapper = LocationEntityMapper()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        repository = LocationRepositoryImpl(dao, service, dtoMapper, entityMapper, testDispatcher)
    }

    // ── loadLocationsPage ─────────────────────────────────────────────────────

    @Test
    fun `loadLocationsPage success stores entities and returns count`() = runTest {
        coEvery { service.getLocations(1, null) } returns successResponse(listOf(buildDto(id = 1)))

        val result = repository.loadLocationsPage(1)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow())
        coVerify { dao.insertAll(any()) }
    }

    @Test
    fun `loadLocationsPage 404 returns zero without inserting`() = runTest {
        coEvery { service.getLocations(1, null) } returns mockErrorResponse(code = 404)

        val result = repository.loadLocationsPage(1)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow())
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `loadLocationsPage network exception returns failure`() = runTest {
        coEvery { service.getLocations(1, null) } throws RuntimeException("Network unavailable")

        val result = repository.loadLocationsPage(1)

        assertTrue(result.isFailure)
    }

    @Test
    fun `loadLocationsPage HTTP 500 returns failure without inserting`() = runTest {
        coEvery { service.getLocations(1, null) } returns mockErrorResponse(code = 500)

        val result = repository.loadLocationsPage(1)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    // ── refreshLocationById ───────────────────────────────────────────────────

    @Test
    fun `refreshLocationById preserves existing pageIndex`() = runTest {
        val existing = buildEntity(id = 1, pageIndex = 3)
        coEvery { service.getLocationById(1) } returns successSingleResponse(buildDto(id = 1))
        coEvery { dao.getById(1) } returns existing

        repository.refreshLocationById(1)

        val slot = slot<LocationEntity>()
        coVerify { dao.insertOrUpdate(capture(slot)) }
        assertEquals(3, slot.captured.pageIndex)
    }

    @Test
    fun `refreshLocationById sets isDetailLoaded to true`() = runTest {
        coEvery { service.getLocationById(1) } returns successSingleResponse(buildDto(id = 1))
        coEvery { dao.getById(1) } returns null

        repository.refreshLocationById(1)

        val slot = slot<LocationEntity>()
        coVerify { dao.insertOrUpdate(capture(slot)) }
        assertTrue(slot.captured.isDetailLoaded)
    }

    @Test
    fun `refreshLocationById returns failure on error`() = runTest {
        coEvery { service.getLocationById(1) } throws RuntimeException("Timeout")

        val result = repository.refreshLocationById(1)

        assertTrue(result.isFailure)
    }

    // ── observeLocations ──────────────────────────────────────────────────────

    @Test
    fun `observeLocations maps dao entities to domain models`() = runTest {
        val entity = buildEntity(id = 7, name = "Citadel of Ricks")
        every { dao.observeAll() } returns flowOf(listOf(entity))

        val result = repository.observeLocations().first()

        assertEquals(1, result.size)
        assertEquals(7, result[0].id)
        assertEquals("Citadel of Ricks", result[0].name)
    }

    @Test
    fun `searchLocations returns full list when query is blank`() = runTest {
        val entity = buildEntity(id = 1)
        every { dao.observeAll() } returns flowOf(listOf(entity))

        val result = repository.searchLocations("").first()

        assertEquals(1, result.size)
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildDto(id: Int = 1, name: String = "Earth (C-137)") = LocationDto(
        id = id,
        name = name,
        type = "Planet",
        dimension = "Dimension C-137",
        residents = listOf("https://rickandmortyapi.com/api/character/1"),
        url = "https://rickandmortyapi.com/api/location/$id",
        created = "2017-11-10T12:42:04.162Z"
    )

    private fun buildEntity(id: Int = 1, name: String = "Earth (C-137)", pageIndex: Int = 0) =
        LocationEntity(
            id = id,
            name = name,
            type = "Planet",
            dimension = "Dimension C-137",
            pageIndex = pageIndex
        )

    private fun successResponse(dtos: List<LocationDto>): Response<ApiResponse<LocationDto>> =
        Response.success(
            ApiResponse(
                info = PageInfoDto(count = 126, pages = 7, next = "next", prev = null),
                results = dtos
            )
        )

    private fun successSingleResponse(dto: LocationDto): Response<LocationDto> =
        Response.success(dto)

    private fun mockErrorResponse(code: Int): Response<ApiResponse<LocationDto>> {
        val response = mockk<Response<ApiResponse<LocationDto>>>()
        every { response.isSuccessful } returns false
        every { response.code() } returns code
        every { response.message() } returns "Error"
        return response
    }
}

