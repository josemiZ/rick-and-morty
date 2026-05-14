package com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail

import androidx.lifecycle.SavedStateHandle
import com.mickyzg.rickandmorty.domain.model.Episode
import com.mickyzg.rickandmorty.domain.repository.EpisodeRepository
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
import org.junit.Rule
import org.junit.Test

class EpisodeDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: EpisodeRepository = mockk(relaxed = true)

    private fun buildSavedStateHandle(id: Int = 1) =
        SavedStateHandle(mapOf(Route.EpisodeDetail.ARG_EPISODE_ID to id))

    // ── Initialization ────────────────────────────────────────────────────────

    @Test
    fun `init triggers refreshDetail when episode not in cache`() = runTest {
        every { repository.observeEpisodeById(1) } returns flowOf(null)
        coEvery { repository.refreshEpisodeById(1) } returns Result.success(Unit)

        EpisodeDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify { repository.refreshEpisodeById(1) }
    }

    @Test
    fun `init triggers refreshDetail when isDetailLoaded is false`() = runTest {
        val cached = buildEpisode(id = 1, isDetailLoaded = false)
        every { repository.observeEpisodeById(1) } returns flowOf(cached)
        coEvery { repository.refreshEpisodeById(1) } returns Result.success(Unit)

        EpisodeDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify { repository.refreshEpisodeById(1) }
    }

    @Test
    fun `init does NOT trigger refresh when detail already loaded`() = runTest {
        val fullyLoaded = buildEpisode(id = 1, isDetailLoaded = true)
        every { repository.observeEpisodeById(1) } returns flowOf(fullyLoaded)

        EpisodeDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify(exactly = 0) { repository.refreshEpisodeById(any()) }
    }

    // ── State transitions ─────────────────────────────────────────────────────

    @Test
    fun `episode emitted from repository is reflected in state`() = runTest {
        val episode = buildEpisode(id = 1, isDetailLoaded = true)
        val flow = MutableStateFlow<Episode?>(null)
        every { repository.observeEpisodeById(1) } returns flow
        coEvery { repository.refreshEpisodeById(1) } returns Result.success(Unit)

        val vm = EpisodeDetailViewModel(repository, buildSavedStateHandle(1))
        flow.value = episode

        assertNotNull(vm.state.value.episode)
    }

    @Test
    fun `network error surfaces in state`() = runTest {
        every { repository.observeEpisodeById(1) } returns flowOf(null)
        coEvery { repository.refreshEpisodeById(1) } returns
            Result.failure(RuntimeException("Not found"))

        val vm = EpisodeDetailViewModel(repository, buildSavedStateHandle(1))

        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `isLoading is false after successful fetch`() = runTest {
        every { repository.observeEpisodeById(1) } returns flowOf(null)
        coEvery { repository.refreshEpisodeById(1) } returns Result.success(Unit)

        val vm = EpisodeDetailViewModel(repository, buildSavedStateHandle(1))

        assertFalse(vm.state.value.isLoading)
    }

    // ── Retry ─────────────────────────────────────────────────────────────────

    @Test
    fun `Retry action clears error and retriggers fetch`() = runTest {
        every { repository.observeEpisodeById(1) } returns flowOf(null)
        coEvery { repository.refreshEpisodeById(1) } returns
            Result.failure(RuntimeException("Timeout"))

        val vm = EpisodeDetailViewModel(repository, buildSavedStateHandle(1))
        assertNotNull(vm.state.value.error)

        coEvery { repository.refreshEpisodeById(1) } returns Result.success(Unit)
        vm.publish(EpisodeDetailAction.Retry)

        assertNull(vm.state.value.error)
    }

    @Test
    fun `Retry calls refreshEpisodeById a second time`() = runTest {
        every { repository.observeEpisodeById(1) } returns flowOf(null)
        coEvery { repository.refreshEpisodeById(1) } returns Result.failure(RuntimeException("Timeout"))

        val vm = EpisodeDetailViewModel(repository, buildSavedStateHandle(1))

        coEvery { repository.refreshEpisodeById(1) } returns Result.success(Unit)
        vm.publish(EpisodeDetailAction.Retry)

        coVerify(atLeast = 2) { repository.refreshEpisodeById(1) }
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildEpisode(id: Int = 1, isDetailLoaded: Boolean = false) = Episode(
        id = id,
        name = "Pilot",
        airDate = "December 2, 2013",
        episodeCode = "S01E01",
        isDetailLoaded = isDetailLoaded
    )
}

