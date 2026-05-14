package com.mickyzg.rickandmorty.presentation.viewmodel.episodeList

import com.mickyzg.rickandmorty.domain.model.Episode
import com.mickyzg.rickandmorty.domain.repository.EpisodeRepository
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

class EpisodeListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: EpisodeRepository = mockk(relaxed = true)
    private val episodeFlow = MutableStateFlow<List<Episode>>(emptyList())

    private lateinit var viewModel: EpisodeListViewModel

    @Before
    fun setUp() {
        every { repository.observeEpisodes() } returns episodeFlow
        every { repository.searchEpisodes(any()) } returns flowOf(emptyList())
        coEvery { repository.loadEpisodesPage(any(), any()) } returns Result.success(20)
        viewModel = EpisodeListViewModel(repository)
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
        coVerify { repository.loadEpisodesPage(1, null) }
    }

    @Test
    fun `when cache has items no initial page load triggered`() = runTest {
        episodeFlow.value = listOf(buildEpisode(id = 1))
        every { repository.observeEpisodes() } returns episodeFlow
        val vm = EpisodeListViewModel(repository)
        assertFalse(vm.state.value.isLoading)
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Test
    fun `SearchQueryChanged updates searchQuery in state`() {
        viewModel.publish(EpisodeListAction.SearchQueryChanged("Pilot"))
        assertEquals("Pilot", viewModel.state.value.searchQuery)
    }

    @Test
    fun `SearchQueryChanged resets endReached`() {
        coEvery { repository.loadEpisodesPage(any(), any()) } returns Result.success(0)
        viewModel.publish(EpisodeListAction.Retry)
        assertTrue(viewModel.state.value.endReached)

        viewModel.publish(EpisodeListAction.SearchQueryChanged("Pilot"))
        assertFalse(viewModel.state.value.endReached)
    }

    @Test
    fun `clearing search query resets searchQuery to empty`() {
        viewModel.publish(EpisodeListAction.SearchQueryChanged("Pilot"))
        viewModel.publish(EpisodeListAction.SearchQueryChanged(""))
        assertEquals("", viewModel.state.value.searchQuery)
    }

    // ── Pagination ────────────────────────────────────────────────────────────

    @Test
    fun `LoadMore is no-op when endReached is true`() = runTest {
        coEvery { repository.loadEpisodesPage(any(), any()) } returns Result.success(0)
        viewModel.publish(EpisodeListAction.Retry)
        assertTrue(viewModel.state.value.endReached)

        coEvery { repository.loadEpisodesPage(any(), any()) } returns Result.success(20)
        viewModel.publish(EpisodeListAction.LoadMore)

        assertTrue(viewModel.state.value.endReached)
    }

    @Test
    fun `loadPage with 0 results sets endReached to true`() = runTest {
        coEvery { repository.loadEpisodesPage(any(), any()) } returns Result.success(0)
        viewModel.publish(EpisodeListAction.Retry)
        assertTrue(viewModel.state.value.endReached)
    }

    @Test
    fun `failed page load surfaces error in state`() = runTest {
        coEvery { repository.loadEpisodesPage(any(), any()) } returns
            Result.failure(RuntimeException("No connection"))
        viewModel.publish(EpisodeListAction.Retry)
        assertTrue(viewModel.state.value.error != null)
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    @Test
    fun `Refresh action clears isRefreshing after success`() = runTest {
        viewModel.publish(EpisodeListAction.Refresh)
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `Refresh surfaces error when network fails`() = runTest {
        coEvery { repository.loadEpisodesPage(1, any()) } returns
            Result.failure(RuntimeException("Timeout"))
        viewModel.publish(EpisodeListAction.Refresh)
        assertFalse(viewModel.state.value.isRefreshing)
        assertFalse(viewModel.state.value.error.isNullOrBlank())
    }

    // ── Retry ─────────────────────────────────────────────────────────────────

    @Test
    fun `Retry clears existing error`() = runTest {
        coEvery { repository.loadEpisodesPage(any(), any()) } returns
            Result.failure(RuntimeException("Error"))
        viewModel.publish(EpisodeListAction.Retry)
        assertTrue(viewModel.state.value.error != null)

        coEvery { repository.loadEpisodesPage(any(), any()) } returns Result.success(20)
        viewModel.publish(EpisodeListAction.Retry)
        assertNull(viewModel.state.value.error)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildEpisode(id: Int = 1) = Episode(
        id = id,
        name = "Pilot",
        airDate = "December 2, 2013",
        episodeCode = "S01E01"
    )
}

