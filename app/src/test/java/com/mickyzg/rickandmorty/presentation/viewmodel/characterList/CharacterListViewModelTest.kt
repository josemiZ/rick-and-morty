package com.mickyzg.rickandmorty.presentation.viewmodel.characterList

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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CharacterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CharacterRepository = mockk(relaxed = true)
    private val characterFlow = MutableStateFlow<List<Character>>(emptyList())

    private lateinit var viewModel: CharacterListViewModel

    @Before
    fun setUp() {
        every { repository.observeCharacters() } returns characterFlow
        every { repository.searchCharacters(any()) } returns flowOf(emptyList())
        coEvery { repository.loadCharactersPage(any(), any()) } returns Result.success(20)
        viewModel = CharacterListViewModel(repository)
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial state has default values`() {
        val s = viewModel.state.value
        assertFalse(s.isLoadingMore)
        assertFalse(s.endReached)
        assertEquals("", s.searchQuery)
        assertNull(s.error)
    }

    @Test
    fun `init triggers loadPage 1 when cache is empty`() = runTest {
        coVerify { repository.loadCharactersPage(1, null) }
    }

    @Test
    fun `when cache has items no initial page load triggered`() = runTest {
        characterFlow.value = listOf(buildCharacter(id = 1))
        every { repository.observeCharacters() } returns characterFlow
        val vm = CharacterListViewModel(repository)
        assertFalse(vm.state.value.isLoading)
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Test
    fun `SearchQueryChanged action updates searchQuery in state`() {
        viewModel.publish(CharacterListAction.SearchQueryChanged("Rick"))
        assertEquals("Rick", viewModel.state.value.searchQuery)
    }

    @Test
    fun `SearchQueryChanged resets endReached`() {
        coEvery { repository.loadCharactersPage(any(), any()) } returns Result.success(0)
        viewModel.publish(CharacterListAction.Retry)
        assertTrue(viewModel.state.value.endReached)

        viewModel.publish(CharacterListAction.SearchQueryChanged("Rick"))
        assertFalse(viewModel.state.value.endReached)
    }

    @Test
    fun `clearing search query resets searchQuery to empty`() {
        viewModel.publish(CharacterListAction.SearchQueryChanged("Rick"))
        viewModel.publish(CharacterListAction.SearchQueryChanged(""))
        assertEquals("", viewModel.state.value.searchQuery)
    }

    // ── Pagination ────────────────────────────────────────────────────────────

    @Test
    fun `LoadMore is no-op when endReached is true`() = runTest {
        coEvery { repository.loadCharactersPage(any(), any()) } returns Result.success(0)
        viewModel.publish(CharacterListAction.Retry)
        assertTrue(viewModel.state.value.endReached)

        coEvery { repository.loadCharactersPage(any(), any()) } returns Result.success(20)
        viewModel.publish(CharacterListAction.LoadMore)

        assertTrue(viewModel.state.value.endReached)
    }

    @Test
    fun `loadPage with 0 results sets endReached to true`() = runTest {
        coEvery { repository.loadCharactersPage(any(), any()) } returns Result.success(0)
        viewModel.publish(CharacterListAction.Retry)
        assertTrue(viewModel.state.value.endReached)
    }

    @Test
    fun `failed page load surfaces error in state`() = runTest {
        coEvery { repository.loadCharactersPage(any(), any()) } returns Result.failure(RuntimeException("No connection"))
        viewModel.publish(CharacterListAction.Retry)
        assertTrue(viewModel.state.value.error != null)
    }

    // ── Favorites ─────────────────────────────────────────────────────────────

    @Test
    fun `ToggleFavorite action calls repository setFavorite with correct args`() = runTest {
        coEvery { repository.setFavorite(42, true) } returns Result.success(Unit)
        viewModel.publish(CharacterListAction.ToggleFavorite(42, true))
        coVerify { repository.setFavorite(42, true) }
    }

    @Test
    fun `setFavorite failure surfaces error`() = runTest {
        coEvery { repository.setFavorite(any(), any()) } returns Result.failure(RuntimeException("Not cached"))
        viewModel.publish(CharacterListAction.ToggleFavorite(1, true))
        assertTrue(viewModel.state.value.error != null)
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    @Test
    fun `Refresh action clears isRefreshing after success`() = runTest {
        viewModel.publish(CharacterListAction.Refresh)
        assertFalse(viewModel.state.value.isRefreshing)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildCharacter(id: Int = 1) = Character(
        id = id, name = "Rick $id",
        status = CharacterStatus.ALIVE,
        species = "Human",
        imageUrl = "https://img/$id"
    )
}
