package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mickyzg.rickandmorty.domain.model.Location
import com.mickyzg.rickandmorty.presentation.component.TestTags
import com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail.LocationDetailAction
import com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail.LocationDetailUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LocationDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun loadingState_showsLoadingIndicator() {
        setScreen(LocationDetailUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotShowErrorContent() {
        setScreen(LocationDetailUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertDoesNotExist()
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun errorState_showsErrorContentWithMessage() {
        setScreen(LocationDetailUiState(error = "Not found"))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Not found").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButtonFiresRetryAction() {
        val actions = mutableListOf<LocationDetailAction>()
        setScreen(
            state = LocationDetailUiState(error = "Timeout"),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(actions.contains(LocationDetailAction.Retry))
    }

    // ── Location detail content ───────────────────────────────────────────────

    @Test
    fun locationLoaded_displaysName() {
        setScreen(LocationDetailUiState(location = detailLocation))
        composeTestRule.onAllNodesWithText("Earth (C-137)")[0].assertIsDisplayed()
    }

    @Test
    fun locationLoaded_displaysType() {
        setScreen(LocationDetailUiState(location = detailLocation))
        composeTestRule.onNodeWithText("Type").assertIsDisplayed()
        composeTestRule.onNodeWithText("Planet").assertIsDisplayed()
    }

    @Test
    fun locationLoaded_displaysDimension() {
        setScreen(LocationDetailUiState(location = detailLocation))
        composeTestRule.onNodeWithText("Dimension").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dimension C-137").assertIsDisplayed()
    }

    @Test
    fun locationLoaded_displaysResidentCount() {
        setScreen(LocationDetailUiState(location = detailLocation))
        composeTestRule.onNodeWithText("Residents").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    // ── Back button ───────────────────────────────────────────────────────────

    @Test
    fun backButton_firesOnBackCallback() {
        var backPressed = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                LocationDetailScreen(
                    uiState = LocationDetailUiState(location = detailLocation),
                    onAction = {},
                    onBack = { backPressed = true }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(backPressed)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private val detailLocation = Location(
        id = 1,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137",
        residentUrls = listOf("https://r/1", "https://r/2", "https://r/3", "https://r/4", "https://r/5"),
        isDetailLoaded = true
    )

    private fun setScreen(
        state: LocationDetailUiState,
        onAction: (LocationDetailAction) -> Unit = {},
        onBack: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            RickAndMortyTheme {
                LocationDetailScreen(
                    uiState = state,
                    onAction = onAction,
                    onBack = onBack
                )
            }
        }
    }
}

