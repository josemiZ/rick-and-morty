package com.mickyzg.rickandmorty.data.repository

import com.mickyzg.rickandmorty.data.local.EpisodeDao
import com.mickyzg.rickandmorty.data.local.EpisodeEntity
import com.mickyzg.rickandmorty.data.local.mapper.EpisodeEntityMapper
import com.mickyzg.rickandmorty.data.remote.RickAndMortyService
import com.mickyzg.rickandmorty.data.remote.dto.ApiResponse
import com.mickyzg.rickandmorty.data.remote.dto.EpisodeDto
import com.mickyzg.rickandmorty.data.remote.dto.PageInfoDto
import com.mickyzg.rickandmorty.data.remote.mapper.EpisodeDtoMapper
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class EpisodeRepositoryImplTest {

    private val dao: EpisodeDao = mockk(relaxed = true)
    private val service: RickAndMortyService = mockk()
    private val dtoMapper = EpisodeDtoMapper()
    private val entityMapper = EpisodeEntityMapper()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: EpisodeRepositoryImpl

    @Before
    fun setUp() {
        repository = EpisodeRepositoryImpl(dao, service, dtoMapper, entityMapper, testDispatcher)
    }

    // ── loadEpisodesPage ──────────────────────────────────────────────────────

    @Test
    fun `loadEpisodesPage success stores entities and returns count`() = runTest {
        coEvery { service.getEpisodes(1, null) } returns successResponse(listOf(buildDto(id = 1)))

        val result = repository.loadEpisodesPage(1)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow())
        coVerify { dao.insertAll(any()) }
    }

    @Test
    fun `loadEpisodesPage 404 returns zero without inserting`() = runTest {
        coEvery { service.getEpisodes(1, null) } returns mockErrorResponse(code = 404)

        val result = repository.loadEpisodesPage(1)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow())
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `loadEpisodesPage network exception returns failure`() = runTest {
        coEvery { service.getEpisodes(1, null) } throws RuntimeException("Network unavailable")

        val result = repository.loadEpisodesPage(1)

        assertTrue(result.isFailure)
    }

    @Test
    fun `loadEpisodesPage HTTP 500 returns failure without inserting`() = runTest {
        coEvery { service.getEpisodes(1, null) } returns mockErrorResponse(code = 500)

        val result = repository.loadEpisodesPage(1)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    // ── refreshEpisodeById ────────────────────────────────────────────────────

    @Test
    fun `refreshEpisodeById preserves existing pageIndex`() = runTest {
        val existing = buildEntity(id = 1, pageIndex = 5)
        coEvery { service.getEpisodeById(1) } returns successSingleResponse(buildDto(id = 1))
        coEvery { dao.getById(1) } returns existing

        repository.refreshEpisodeById(1)

        val slot = slot<EpisodeEntity>()
        coVerify { dao.insertOrUpdate(capture(slot)) }
        assertEquals(5, slot.captured.pageIndex)
    }

    @Test
    fun `refreshEpisodeById sets isDetailLoaded to true`() = runTest {
        coEvery { service.getEpisodeById(1) } returns successSingleResponse(buildDto(id = 1))
        coEvery { dao.getById(1) } returns null

        repository.refreshEpisodeById(1)

        val slot = slot<EpisodeEntity>()
        coVerify { dao.insertOrUpdate(capture(slot)) }
        assertTrue(slot.captured.isDetailLoaded)
    }

    @Test
    fun `refreshEpisodeById returns failure on error`() = runTest {
        coEvery { service.getEpisodeById(1) } throws RuntimeException("Timeout")

        val result = repository.refreshEpisodeById(1)

        assertTrue(result.isFailure)
    }

    // ── observeEpisodes ───────────────────────────────────────────────────────

    @Test
    fun `observeEpisodes maps dao entities to domain models`() = runTest {
        val entity = buildEntity(id = 3, name = "Pickle Rick")
        every { dao.observeAll() } returns flowOf(listOf(entity))

        val result = repository.observeEpisodes().first()

        assertEquals(1, result.size)
        assertEquals(3, result[0].id)
        assertEquals("Pickle Rick", result[0].name)
    }

    @Test
    fun `searchEpisodes returns full list when query is blank`() = runTest {
        val entity = buildEntity(id = 1)
        every { dao.observeAll() } returns flowOf(listOf(entity))

        val result = repository.searchEpisodes("").first()

        assertEquals(1, result.size)
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildDto(id: Int = 1, name: String = "Pilot") = EpisodeDto(
        id = id,
        name = name,
        airDate = "December 2, 2013",
        episodeCode = "S01E01",
        characters = listOf("https://rickandmortyapi.com/api/character/1"),
        url = "https://rickandmortyapi.com/api/episode/$id",
        created = "2017-11-10T12:56:33.798Z"
    )

    private fun buildEntity(id: Int = 1, name: String = "Pilot", pageIndex: Int = 0) =
        EpisodeEntity(
            id = id,
            name = name,
            airDate = "December 2, 2013",
            episodeCode = "S01E01",
            pageIndex = pageIndex
        )

    private fun successResponse(dtos: List<EpisodeDto>): Response<ApiResponse<EpisodeDto>> =
        Response.success(
            ApiResponse(
                info = PageInfoDto(count = 51, pages = 3, next = "next", prev = null),
                results = dtos
            )
        )

    private fun successSingleResponse(dto: EpisodeDto): Response<EpisodeDto> =
        Response.success(dto)

    private fun mockErrorResponse(code: Int): Response<ApiResponse<EpisodeDto>> {
        val response = mockk<Response<ApiResponse<EpisodeDto>>>()
        every { response.isSuccessful } returns false
        every { response.code() } returns code
        every { response.message() } returns "Error"
        return response
    }
}

