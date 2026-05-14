package com.mickyzg.rickandmorty.data.repository

import com.mickyzg.rickandmorty.data.local.CharacterDao
import com.mickyzg.rickandmorty.data.local.CharacterEntity
import com.mickyzg.rickandmorty.data.local.mapper.CharacterEntityMapper
import com.mickyzg.rickandmorty.data.remote.RickAndMortyService
import com.mickyzg.rickandmorty.data.remote.dto.ApiResponse
import com.mickyzg.rickandmorty.data.remote.dto.CharacterDto
import com.mickyzg.rickandmorty.data.remote.dto.CharacterLocationDto
import com.mickyzg.rickandmorty.data.remote.dto.PageInfoDto
import com.mickyzg.rickandmorty.data.remote.mapper.CharacterDtoMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CharacterRepositoryImplTest {

    private val dao: CharacterDao = mockk(relaxed = true)
    private val service: RickAndMortyService = mockk()
    private val dtoMapper = CharacterDtoMapper()
    private val entityMapper = CharacterEntityMapper()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: CharacterRepositoryImpl

    @Before
    fun setUp() {
        repository = CharacterRepositoryImpl(dao, service, dtoMapper, entityMapper, testDispatcher)
    }

    // ── loadCharactersPage ────────────────────────────────────────────────────

    @Test
    fun `loadCharactersPage success stores entities and returns count`() = runTest {
        val dto = buildDto(id = 1)
        coEvery { service.getCharacters(1, null) } returns successResponse(listOf(dto))
        coEvery { dao.getFavoriteIds() } returns emptyList()

        val result = repository.loadCharactersPage(1)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow())
        coVerify { dao.insertAll(any()) }
    }

    @Test
    fun `loadCharactersPage preserves isFavorite for existing items`() = runTest {
        val dto = buildDto(id = 1)
        coEvery { service.getCharacters(1, null) } returns successResponse(listOf(dto))
        coEvery { dao.getFavoriteIds() } returns listOf(1) // character 1 is a favorite

        val slot = slot<List<CharacterEntity>>()
        coEvery { dao.insertAll(capture(slot)) } returns Unit

        repository.loadCharactersPage(1)

        assertTrue("isFavorite should be preserved", slot.captured.first().isFavorite)
    }

    @Test
    fun `loadCharactersPage 404 returns zero without inserting`() = runTest {
        val response = mockErrorResponse(code = 404)
        coEvery { service.getCharacters(1, null) } returns response

        val result = repository.loadCharactersPage(1)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow())
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `loadCharactersPage network exception returns failure`() = runTest {
        coEvery { service.getCharacters(1, null) } throws RuntimeException("Network unavailable")

        val result = repository.loadCharactersPage(1)

        assertTrue(result.isFailure)
    }

    @Test
    fun `loadCharactersPage HTTP 500 returns failure without inserting`() = runTest {
        coEvery { service.getCharacters(1, null) } returns mockErrorResponse(code = 500)

        val result = repository.loadCharactersPage(1)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    // ── refreshCharacterById ──────────────────────────────────────────────────

    @Test
    fun `refreshCharacterById preserves existing isFavorite and pageIndex`() = runTest {
        val existing = buildEntity(id = 1, isFavorite = true, pageIndex = 7)
        coEvery { service.getCharacterById(1) } returns successSingleResponse(buildDto(id = 1))
        coEvery { dao.getById(1) } returns existing

        repository.refreshCharacterById(1)

        val slot = slot<CharacterEntity>()
        coVerify { dao.insertOrUpdate(capture(slot)) }
        assertTrue("isFavorite must be preserved", slot.captured.isFavorite)
        assertEquals(7, slot.captured.pageIndex)
    }

    @Test
    fun `refreshCharacterById sets isDetailLoaded to true`() = runTest {
        coEvery { service.getCharacterById(1) } returns successSingleResponse(buildDto(id = 1))
        coEvery { dao.getById(1) } returns null // not previously cached

        repository.refreshCharacterById(1)

        val slot = slot<CharacterEntity>()
        coVerify { dao.insertOrUpdate(capture(slot)) }
        assertTrue(slot.captured.isDetailLoaded)
    }

    @Test
    fun `refreshCharacterById returns failure on error`() = runTest {
        coEvery { service.getCharacterById(1) } throws RuntimeException("Timeout")

        val result = repository.refreshCharacterById(1)

        assertTrue(result.isFailure)
    }

    // ── setFavorite ───────────────────────────────────────────────────────────

    @Test
    fun `setFavorite calls dao updateFavorite when character cached`() = runTest {
        coEvery { dao.exists(1) } returns 1

        val result = repository.setFavorite(1, true)

        assertTrue(result.isSuccess)
        coVerify { dao.updateFavorite(1, true) }
    }

    @Test
    fun `setFavorite returns failure when character not in cache`() = runTest {
        coEvery { dao.exists(99) } returns 0

        val result = repository.setFavorite(99, true)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { dao.updateFavorite(any(), any()) }
    }

    // ── observeCharacters ─────────────────────────────────────────────────────

    @Test
    fun `observeCharacters maps dao entities to domain models`() = runTest {
        val entity = buildEntity(id = 5, name = "Jerry")
        every { dao.observeAll() } returns flowOf(listOf(entity))

        val result = repository.observeCharacters().first()

        assertEquals(1, result.size)
        assertEquals(5, result[0].id)
        assertEquals("Jerry", result[0].name)
    }

    @Test
    fun `searchCharacters returns full list when query is blank`() = runTest {
        val entity = buildEntity(id = 1)
        every { dao.observeAll() } returns flowOf(listOf(entity))

        val result = repository.searchCharacters("").first()

        assertEquals(1, result.size)
    }

    @Test
    fun `observeFavorites delegates to dao observeFavorites`() = runTest {
        val favorite = buildEntity(id = 1, isFavorite = true)
        every { dao.observeFavorites() } returns flowOf(listOf(favorite))

        val result = repository.observeFavorites().first()

        assertEquals(1, result.size)
        assertTrue(result[0].isFavorite)
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildDto(id: Int = 1, name: String = "Rick") = CharacterDto(
        id = id, name = name, status = "Alive", species = "Human", type = "",
        gender = "Male",
        origin = CharacterLocationDto("Earth", "https://loc/1"),
        location = CharacterLocationDto("Citadel", "https://loc/3"),
        image = "https://img/$id", episode = listOf("https://ep/1"),
        url = "https://char/$id", created = "2017-11-04T18:48:46.250Z"
    )

    private fun buildEntity(
        id: Int = 1,
        name: String = "Rick",
        isFavorite: Boolean = false,
        pageIndex: Int = 0
    ) = CharacterEntity(
        id = id, name = name, status = "Alive", species = "Human",
        imageUrl = "https://img/$id", isFavorite = isFavorite, pageIndex = pageIndex
    )

    private fun successResponse(dtos: List<CharacterDto>): Response<ApiResponse<CharacterDto>> =
        Response.success(
            ApiResponse(
                info = PageInfoDto(count = 100, pages = 5, next = "next", prev = null),
                results = dtos
            )
        )

    private fun successSingleResponse(dto: CharacterDto): Response<CharacterDto> =
        Response.success(dto)

    private fun mockErrorResponse(code: Int): Response<ApiResponse<CharacterDto>> {
        val response = mockk<Response<ApiResponse<CharacterDto>>>()
        every { response.isSuccessful } returns false
        every { response.code() } returns code
        every { response.message() } returns "Error"
        return response
    }
}

