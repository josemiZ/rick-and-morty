package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mickyzg.rickandmorty.domain.model.Episode
import com.mickyzg.rickandmorty.presentation.component.TestTags
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail.EpisodeDetailAction
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail.EpisodeDetailUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EpisodeDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun loadingState_showsLoadingIndicator() {
        setScreen(EpisodeDetailUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotShowErrorContent() {
        setScreen(EpisodeDetailUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertDoesNotExist()
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun errorState_showsErrorContentWithMessage() {
        setScreen(EpisodeDetailUiState(error = "Episode not found"))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Episode not found").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButtonFiresRetryAction() {
        val actions = mutableListOf<EpisodeDetailAction>()
        setScreen(
            state = EpisodeDetailUiState(error = "Network error"),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(actions.contains(EpisodeDetailAction.Retry))
    }

    // ── Episode detail content ────────────────────────────────────────────────

    @Test
    fun episodeLoaded_displaysName() {
        setScreen(EpisodeDetailUiState(episode = detailEpisode))
        composeTestRule.onAllNodesWithText("Pilot")[0].assertIsDisplayed()
    }

    @Test
    fun episodeLoaded_displaysEpisodeCode() {
        setScreen(EpisodeDetailUiState(episode = detailEpisode))
        composeTestRule.onNodeWithText("S01E01").assertIsDisplayed()
    }

    @Test
    fun episodeLoaded_displaysAirDate() {
        setScreen(EpisodeDetailUiState(episode = detailEpisode))
        composeTestRule.onNodeWithText("Air date").assertIsDisplayed()
        composeTestRule.onNodeWithText("December 2, 2013").assertIsDisplayed()
    }

    @Test
    fun episodeLoaded_displaysCharacterCount() {
        setScreen(EpisodeDetailUiState(episode = detailEpisode))
        composeTestRule.onNodeWithText("Characters").assertIsDisplayed()
        composeTestRule.onNodeWithText("9").assertIsDisplayed()
    }

    // ── Back button ───────────────────────────────────────────────────────────

    @Test
    fun backButton_firesOnBackCallback() {
        var backPressed = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                EpisodeDetailScreen(
                    uiState = EpisodeDetailUiState(episode = detailEpisode),
                    onAction = {},
                    onBack = { backPressed = true }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(backPressed)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private val detailEpisode = Episode(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        episodeCode = "S01E01",
        characterUrls = (1..9).map { "https://rickandmortyapi.com/api/character/$it" },
        isDetailLoaded = true
    )

    private fun setScreen(
        state: EpisodeDetailUiState,
        onAction: (EpisodeDetailAction) -> Unit = {},
        onBack: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            RickAndMortyTheme {
                EpisodeDetailScreen(
                    uiState = state,
                    onAction = onAction,
                    onBack = onBack
                )
            }
        }
    }
}

