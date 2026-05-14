package com.mickyzg.rickandmorty.presentation.viewmodel.favorites

import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import com.mickyzg.rickandmorty.domain.repository.CharacterRepository
import com.mickyzg.rickandmorty.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CharacterRepository = mockk(relaxed = true)
    private val favoritesFlow = MutableStateFlow<List<Character>>(emptyList())

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        every { repository.observeFavorites() } returns favoritesFlow
        viewModel = FavoritesViewModel(repository)
    }

    // ── Initialization ────────────────────────────────────────────────────────

    @Test
    fun `initial state isLoading is false after emission`() {
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `favorites emitted from repository appear in state`() = runTest {
        val favorites = listOf(buildCharacter(id = 1), buildCharacter(id = 2))
        favoritesFlow.value = favorites

        assertEquals(2, viewModel.state.value.favorites.size)
    }

    @Test
    fun `empty favorites list is reflected in state`() = runTest {
        favoritesFlow.value = emptyList()
        assertEquals(0, viewModel.state.value.favorites.size)
    }

    // ── Updates ───────────────────────────────────────────────────────────────

    @Test
    fun `favorites list updates when repository emits new list`() = runTest {
        favoritesFlow.value = listOf(buildCharacter(id = 1))
        assertEquals(1, viewModel.state.value.favorites.size)

        favoritesFlow.value = listOf(buildCharacter(id = 1), buildCharacter(id = 2))
        assertEquals(2, viewModel.state.value.favorites.size)
    }

    // ── RemoveFavorite ────────────────────────────────────────────────────────

    @Test
    fun `RemoveFavorite action calls setFavorite with false`() = runTest {
        coEvery { repository.setFavorite(1, false) } returns Result.success(Unit)

        viewModel.publish(FavoritesAction.RemoveFavorite(characterId = 1))

        coVerify { repository.setFavorite(1, false) }
    }

    @Test
    fun `RemoveFavorite with correct id calls repository`() = runTest {
        coEvery { repository.setFavorite(42, false) } returns Result.success(Unit)

        viewModel.publish(FavoritesAction.RemoveFavorite(characterId = 42))

        coVerify { repository.setFavorite(42, false) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildCharacter(id: Int = 1) = Character(
        id = id,
        name = "Rick $id",
        status = CharacterStatus.ALIVE,
        species = "Human",
        imageUrl = "https://img/$id",
        isFavorite = true
    )
}

