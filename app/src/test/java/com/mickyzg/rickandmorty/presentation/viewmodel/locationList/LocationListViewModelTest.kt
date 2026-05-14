package com.mickyzg.rickandmorty.presentation.viewmodel.locationList

import com.mickyzg.rickandmorty.domain.model.Location
import com.mickyzg.rickandmorty.domain.repository.LocationRepository
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

class LocationListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LocationRepository = mockk(relaxed = true)
    private val locationFlow = MutableStateFlow<List<Location>>(emptyList())

    private lateinit var viewModel: LocationListViewModel

    @Before
    fun setUp() {
        every { repository.observeLocations() } returns locationFlow
        every { repository.searchLocations(any()) } returns flowOf(emptyList())
        coEvery { repository.loadLocationsPage(any(), any()) } returns Result.success(20)
        viewModel = LocationListViewModel(repository)
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
        coVerify { repository.loadLocationsPage(1, null) }
    }

    @Test
    fun `when cache has items no initial page load triggered`() = runTest {
        locationFlow.value = listOf(buildLocation(id = 1))
        every { repository.observeLocations() } returns locationFlow
        val vm = LocationListViewModel(repository)
        assertFalse(vm.state.value.isLoading)
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Test
    fun `SearchQueryChanged updates searchQuery in state`() {
        viewModel.publish(LocationListAction.SearchQueryChanged("Earth"))
        assertEquals("Earth", viewModel.state.value.searchQuery)
    }

    @Test
    fun `SearchQueryChanged resets endReached`() {
        coEvery { repository.loadLocationsPage(any(), any()) } returns Result.success(0)
        viewModel.publish(LocationListAction.Retry)
        assertTrue(viewModel.state.value.endReached)

        viewModel.publish(LocationListAction.SearchQueryChanged("Earth"))
        assertFalse(viewModel.state.value.endReached)
    }

    @Test
    fun `clearing search query resets searchQuery to empty`() {
        viewModel.publish(LocationListAction.SearchQueryChanged("Earth"))
        viewModel.publish(LocationListAction.SearchQueryChanged(""))
        assertEquals("", viewModel.state.value.searchQuery)
    }

    // ── Pagination ────────────────────────────────────────────────────────────

    @Test
    fun `LoadMore is no-op when endReached is true`() = runTest {
        coEvery { repository.loadLocationsPage(any(), any()) } returns Result.success(0)
        viewModel.publish(LocationListAction.Retry)
        assertTrue(viewModel.state.value.endReached)

        coEvery { repository.loadLocationsPage(any(), any()) } returns Result.success(20)
        viewModel.publish(LocationListAction.LoadMore)

        assertTrue(viewModel.state.value.endReached)
    }

    @Test
    fun `loadPage with 0 results sets endReached to true`() = runTest {
        coEvery { repository.loadLocationsPage(any(), any()) } returns Result.success(0)
        viewModel.publish(LocationListAction.Retry)
        assertTrue(viewModel.state.value.endReached)
    }

    @Test
    fun `failed page load surfaces error in state`() = runTest {
        coEvery { repository.loadLocationsPage(any(), any()) } returns
            Result.failure(RuntimeException("No connection"))
        viewModel.publish(LocationListAction.Retry)
        assertTrue(viewModel.state.value.error != null)
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    @Test
    fun `Refresh action clears isRefreshing after success`() = runTest {
        viewModel.publish(LocationListAction.Refresh)
        assertFalse(viewModel.state.value.isRefreshing)
    }

    @Test
    fun `Refresh surfaces error when network fails`() = runTest {
        coEvery { repository.loadLocationsPage(1, any()) } returns
            Result.failure(RuntimeException("Timeout"))
        viewModel.publish(LocationListAction.Refresh)
        assertFalse(viewModel.state.value.isRefreshing)
        assertFalse(viewModel.state.value.error.isNullOrBlank())
    }

    // ── Retry ─────────────────────────────────────────────────────────────────

    @Test
    fun `Retry clears existing error`() = runTest {
        coEvery { repository.loadLocationsPage(any(), any()) } returns
            Result.failure(RuntimeException("Error"))
        viewModel.publish(LocationListAction.Retry)
        assertTrue(viewModel.state.value.error != null)

        coEvery { repository.loadLocationsPage(any(), any()) } returns Result.success(20)
        viewModel.publish(LocationListAction.Retry)
        assertNull(viewModel.state.value.error)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildLocation(id: Int = 1) = Location(
        id = id,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137"
    )
}

