package com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail

import androidx.lifecycle.SavedStateHandle
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterLocation
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import com.mickyzg.rickandmorty.domain.repository.CharacterRepository
import com.mickyzg.rickandmorty.presentation.navigation.Route
import com.mickyzg.rickandmorty.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CharacterRepository = mockk(relaxed = true)

    private fun buildSavedStateHandle(id: Int = 1) =
        SavedStateHandle(mapOf(Route.CharacterDetail.ARG_CHARACTER_ID to id))

    // ── Initialization ────────────────────────────────────────────────────────

    @Test
    fun `init triggers refreshDetail when character not in cache`() = runTest {
        every { repository.observeCharacterById(1) } returns flowOf(null)
        coEvery { repository.refreshCharacterById(1) } returns Result.success(Unit)

        CharacterDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify { repository.refreshCharacterById(1) }
    }

    @Test
    fun `init triggers refreshDetail when isDetailLoaded is false`() = runTest {
        val cachedListItem = buildCharacter(id = 1, isDetailLoaded = false)
        every { repository.observeCharacterById(1) } returns flowOf(cachedListItem)
        coEvery { repository.refreshCharacterById(1) } returns Result.success(Unit)

        CharacterDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify { repository.refreshCharacterById(1) }
    }

    @Test
    fun `init does NOT trigger refresh when detail already loaded`() = runTest {
        val fullyLoaded = buildCharacter(id = 1, isDetailLoaded = true)
        every { repository.observeCharacterById(1) } returns flowOf(fullyLoaded)

        CharacterDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify(exactly = 0) { repository.refreshCharacterById(any()) }
    }

    // ── State transitions ─────────────────────────────────────────────────────

    @Test
    fun `character emitted from repository is reflected in state`() = runTest {
        val character = buildCharacter(id = 1, isDetailLoaded = true)
        val flow = MutableStateFlow<Character?>(null)
        every { repository.observeCharacterById(1) } returns flow
        coEvery { repository.refreshCharacterById(1) } returns Result.success(Unit)

        val vm = CharacterDetailViewModel(repository, buildSavedStateHandle(1))
        flow.value = character

        assertNotNull(vm.state.value.character)
    }

    @Test
    fun `network error surfaces in state`() = runTest {
        every { repository.observeCharacterById(1) } returns flowOf(null)
        coEvery { repository.refreshCharacterById(1) } returns
            Result.failure(RuntimeException("Not found"))

        val vm = CharacterDetailViewModel(repository, buildSavedStateHandle(1))

        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `Retry action clears error and retriggers fetch`() = runTest {
        every { repository.observeCharacterById(1) } returns flowOf(null)
        coEvery { repository.refreshCharacterById(1) } returns
            Result.failure(RuntimeException("Timeout"))

        val vm = CharacterDetailViewModel(repository, buildSavedStateHandle(1))
        assertNotNull(vm.state.value.error)

        coEvery { repository.refreshCharacterById(1) } returns Result.success(Unit)
        vm.publish(CharacterDetailAction.Retry)

        assertNull(vm.state.value.error)
    }

    // ── ToggleFavorite ────────────────────────────────────────────────────────

    @Test
    fun `ToggleFavorite action calls setFavorite with inverted value`() = runTest {
        val character = buildCharacter(id = 1, isFavorite = false, isDetailLoaded = true)
        every { repository.observeCharacterById(1) } returns flowOf(character)
        coEvery { repository.setFavorite(1, true) } returns Result.success(Unit)

        val vm = CharacterDetailViewModel(repository, buildSavedStateHandle(1))
        vm.publish(CharacterDetailAction.ToggleFavorite)

        coVerify { repository.setFavorite(1, true) }
    }

    @Test
    fun `ToggleFavorite is no-op when character is null`() = runTest {
        every { repository.observeCharacterById(1) } returns flowOf(null)
        coEvery { repository.refreshCharacterById(1) } returns Result.success(Unit)

        val vm = CharacterDetailViewModel(repository, buildSavedStateHandle(1))
        vm.publish(CharacterDetailAction.ToggleFavorite)

        coVerify(exactly = 0) { repository.setFavorite(any(), any()) }
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildCharacter(
        id: Int = 1,
        isFavorite: Boolean = false,
        isDetailLoaded: Boolean = false
    ) = Character(
        id = id, name = "Rick $id",
        status = CharacterStatus.ALIVE, species = "Human",
        imageUrl = "https://img/$id",
        isFavorite = isFavorite,
        origin = CharacterLocation("Earth", "https://loc/1"),
        location = CharacterLocation("Citadel", "https://loc/3"),
        isDetailLoaded = isDetailLoaded
    )
}
