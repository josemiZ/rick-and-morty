package com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail

import androidx.lifecycle.SavedStateHandle
import com.mickyzg.rickandmorty.domain.model.Location
import com.mickyzg.rickandmorty.domain.repository.LocationRepository
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

class LocationDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LocationRepository = mockk(relaxed = true)

    private fun buildSavedStateHandle(id: Int = 1) =
        SavedStateHandle(mapOf(Route.LocationDetail.ARG_LOCATION_ID to id))

    // ── Initialization ────────────────────────────────────────────────────────

    @Test
    fun `init triggers refreshDetail when location not in cache`() = runTest {
        every { repository.observeLocationById(1) } returns flowOf(null)
        coEvery { repository.refreshLocationById(1) } returns Result.success(Unit)

        LocationDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify { repository.refreshLocationById(1) }
    }

    @Test
    fun `init triggers refreshDetail when isDetailLoaded is false`() = runTest {
        val cached = buildLocation(id = 1, isDetailLoaded = false)
        every { repository.observeLocationById(1) } returns flowOf(cached)
        coEvery { repository.refreshLocationById(1) } returns Result.success(Unit)

        LocationDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify { repository.refreshLocationById(1) }
    }

    @Test
    fun `init does NOT trigger refresh when detail already loaded`() = runTest {
        val fullyLoaded = buildLocation(id = 1, isDetailLoaded = true)
        every { repository.observeLocationById(1) } returns flowOf(fullyLoaded)

        LocationDetailViewModel(repository, buildSavedStateHandle(1))

        coVerify(exactly = 0) { repository.refreshLocationById(any()) }
    }

    // ── State transitions ─────────────────────────────────────────────────────

    @Test
    fun `location emitted from repository is reflected in state`() = runTest {
        val location = buildLocation(id = 1, isDetailLoaded = true)
        val flow = MutableStateFlow<Location?>(null)
        every { repository.observeLocationById(1) } returns flow
        coEvery { repository.refreshLocationById(1) } returns Result.success(Unit)

        val vm = LocationDetailViewModel(repository, buildSavedStateHandle(1))
        flow.value = location

        assertNotNull(vm.state.value.location)
    }

    @Test
    fun `network error surfaces in state`() = runTest {
        every { repository.observeLocationById(1) } returns flowOf(null)
        coEvery { repository.refreshLocationById(1) } returns
            Result.failure(RuntimeException("Network error"))

        val vm = LocationDetailViewModel(repository, buildSavedStateHandle(1))

        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `isLoading is false after successful fetch`() = runTest {
        every { repository.observeLocationById(1) } returns flowOf(null)
        coEvery { repository.refreshLocationById(1) } returns Result.success(Unit)

        val vm = LocationDetailViewModel(repository, buildSavedStateHandle(1))

        assertFalse(vm.state.value.isLoading)
    }

    // ── Retry ─────────────────────────────────────────────────────────────────

    @Test
    fun `Retry action clears error and retriggers fetch`() = runTest {
        every { repository.observeLocationById(1) } returns flowOf(null)
        coEvery { repository.refreshLocationById(1) } returns
            Result.failure(RuntimeException("Timeout"))

        val vm = LocationDetailViewModel(repository, buildSavedStateHandle(1))
        assertNotNull(vm.state.value.error)

        coEvery { repository.refreshLocationById(1) } returns Result.success(Unit)
        vm.publish(LocationDetailAction.Retry)

        assertNull(vm.state.value.error)
    }

    @Test
    fun `Retry calls refreshLocationById a second time`() = runTest {
        every { repository.observeLocationById(1) } returns flowOf(null)
        coEvery { repository.refreshLocationById(1) } returns Result.failure(RuntimeException("Timeout"))

        val vm = LocationDetailViewModel(repository, buildSavedStateHandle(1))

        coEvery { repository.refreshLocationById(1) } returns Result.success(Unit)
        vm.publish(LocationDetailAction.Retry)

        coVerify(atLeast = 2) { repository.refreshLocationById(1) }
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun buildLocation(id: Int = 1, isDetailLoaded: Boolean = false) = Location(
        id = id,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137",
        isDetailLoaded = isDetailLoaded
    )
}

