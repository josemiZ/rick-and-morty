package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.mickyzg.rickandmorty.domain.model.Location
import com.mickyzg.rickandmorty.presentation.component.TestTags
import com.mickyzg.rickandmorty.presentation.viewmodel.locationList.LocationListAction
import com.mickyzg.rickandmorty.presentation.viewmodel.locationList.LocationListUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LocationListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Empty state ───────────────────────────────────────────────────────────

    @Test
    fun emptyState_showsEmptyContent() {
        setScreen(LocationListUiState(locations = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertIsDisplayed()
    }

    @Test
    fun emptyState_showsNoLocationsFoundText() {
        setScreen(LocationListUiState(locations = emptyList()))
        composeTestRule.onNodeWithText("No locations found").assertIsDisplayed()
    }

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun loadingState_doesNotShowListOrEmptyOrError() {
        setScreen(LocationListUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.LOCATION_LIST).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertDoesNotExist()
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun errorState_showsErrorContentWithMessage() {
        setScreen(LocationListUiState(error = "Network error", locations = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButtonFiresRetryAction() {
        val actions = mutableListOf<LocationListAction>()
        setScreen(
            state = LocationListUiState(error = "Timeout", locations = emptyList()),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(actions.contains(LocationListAction.Retry))
    }

    // ── Location list ─────────────────────────────────────────────────────────

    @Test
    fun locationList_rendersAllCards() {
        setScreen(LocationListUiState(locations = threeLocations))
        composeTestRule.onNodeWithTag(TestTags.LOCATION_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.locationCard(1)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.locationCard(2)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.locationCard(3)).assertIsDisplayed()
    }

    @Test
    fun locationList_displaysLocationNames() {
        setScreen(LocationListUiState(locations = threeLocations))
        composeTestRule.onNodeWithText("Earth (C-137)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Citadel of Ricks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Anatomy Park").assertIsDisplayed()
    }

    @Test
    fun locationList_displaysSubtitle() {
        setScreen(LocationListUiState(locations = threeLocations))
        // LocationCard formats "type · dimension" as subtitle
        composeTestRule.onNodeWithText("Planet · Dimension C-137").assertIsDisplayed()
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Test
    fun searchField_typingText_firesSearchQueryChangedAction() {
        val actions = mutableListOf<LocationListAction>()
        setScreen(
            state = LocationListUiState(locations = threeLocations),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithTag(TestTags.LOCATION_SEARCH_FIELD).performTextReplacement("Earth")
        val searchActions = actions.filterIsInstance<LocationListAction.SearchQueryChanged>()
        assertTrue(searchActions.isNotEmpty())
        assertEquals("Earth", searchActions.last().query)
    }

    @Test
    fun searchField_clearButton_firesEmptySearchQuery() {
        val actions = mutableListOf<LocationListAction>()
        setScreen(
            state = LocationListUiState(locations = threeLocations, searchQuery = "Earth"),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        val clearActions = actions.filterIsInstance<LocationListAction.SearchQueryChanged>()
        assertTrue(clearActions.any { it.query == "" })
    }

    // ── Card click ────────────────────────────────────────────────────────────

    @Test
    fun locationCard_clickFiresOnLocationClickCallback() {
        var clickedId: Int? = null
        composeTestRule.setContent {
            RickAndMortyTheme {
                LocationListScreen(
                    uiState = LocationListUiState(locations = threeLocations),
                    onAction = {},
                    onLocationClick = { clickedId = it }
                )
            }
        }
        composeTestRule.onNodeWithTag(TestTags.locationCard(2)).performClick()
        assertEquals(2, clickedId)
    }

    // ── List not shown when empty ─────────────────────────────────────────────

    @Test
    fun emptyState_doesNotShowLocationList() {
        setScreen(LocationListUiState(locations = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.LOCATION_LIST).assertDoesNotExist()
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private val threeLocations = listOf(
        Location(id = 1, name = "Earth (C-137)", type = "Planet", dimension = "Dimension C-137"),
        Location(id = 2, name = "Citadel of Ricks", type = "Space station", dimension = "unknown"),
        Location(id = 3, name = "Anatomy Park", type = "Microverse", dimension = "Dimension C-137")
    )

    private fun setScreen(
        state: LocationListUiState,
        onAction: (LocationListAction) -> Unit = {},
        onLocationClick: (Int) -> Unit = {}
    ) {
        composeTestRule.setContent {
            RickAndMortyTheme {
                LocationListScreen(
                    uiState = state,
                    onAction = onAction,
                    onLocationClick = onLocationClick
                )
            }
        }
    }
}

