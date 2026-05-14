package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.mickyzg.rickandmorty.domain.model.Episode
import com.mickyzg.rickandmorty.presentation.component.TestTags
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeList.EpisodeListAction
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeList.EpisodeListUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EpisodeListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Empty state ───────────────────────────────────────────────────────────

    @Test
    fun emptyState_showsEmptyContent() {
        setScreen(EpisodeListUiState(episodes = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertIsDisplayed()
    }

    @Test
    fun emptyState_showsNoEpisodesFoundText() {
        setScreen(EpisodeListUiState(episodes = emptyList()))
        composeTestRule.onNodeWithText("No episodes found").assertIsDisplayed()
    }

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun loadingState_doesNotShowListOrEmptyOrError() {
        setScreen(EpisodeListUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.EPISODE_LIST).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertDoesNotExist()
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun errorState_showsErrorContentWithMessage() {
        setScreen(EpisodeListUiState(error = "Server error", episodes = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Server error").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButtonFiresRetryAction() {
        val actions = mutableListOf<EpisodeListAction>()
        setScreen(
            state = EpisodeListUiState(error = "Timeout", episodes = emptyList()),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(actions.contains(EpisodeListAction.Retry))
    }

    // ── Episode list ──────────────────────────────────────────────────────────

    @Test
    fun episodeList_rendersAllCards() {
        setScreen(EpisodeListUiState(episodes = threeEpisodes))
        composeTestRule.onNodeWithTag(TestTags.EPISODE_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.episodeCard(1)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.episodeCard(2)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.episodeCard(3)).assertIsDisplayed()
    }

    @Test
    fun episodeList_displaysEpisodeNames() {
        setScreen(EpisodeListUiState(episodes = threeEpisodes))
        composeTestRule.onNodeWithText("Pilot").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lawnmower Dog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Anatomy Park").assertIsDisplayed()
    }

    @Test
    fun episodeList_displaysEpisodeCodeAndAirDate() {
        setScreen(EpisodeListUiState(episodes = threeEpisodes))
        // EpisodeCard formats "${episodeCode} · ${airDate}"
        composeTestRule.onNodeWithText("S01E01 · December 2, 2013").assertIsDisplayed()
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @Test
    fun searchField_typingText_firesSearchQueryChangedAction() {
        val actions = mutableListOf<EpisodeListAction>()
        setScreen(
            state = EpisodeListUiState(episodes = threeEpisodes),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithTag(TestTags.EPISODE_SEARCH_FIELD).performTextReplacement("Pilot")
        val searchActions = actions.filterIsInstance<EpisodeListAction.SearchQueryChanged>()
        assertTrue(searchActions.isNotEmpty())
        assertEquals("Pilot", searchActions.last().query)
    }

    @Test
    fun searchField_clearButton_firesEmptySearchQuery() {
        val actions = mutableListOf<EpisodeListAction>()
        setScreen(
            state = EpisodeListUiState(episodes = threeEpisodes, searchQuery = "Pilot"),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        val clearActions = actions.filterIsInstance<EpisodeListAction.SearchQueryChanged>()
        assertTrue(clearActions.any { it.query == "" })
    }

    // ── Card click ────────────────────────────────────────────────────────────

    @Test
    fun episodeCard_clickFiresOnEpisodeClickCallback() {
        var clickedId: Int? = null
        composeTestRule.setContent {
            RickAndMortyTheme {
                EpisodeListScreen(
                    uiState = EpisodeListUiState(episodes = threeEpisodes),
                    onAction = {},
                    onEpisodeClick = { clickedId = it }
                )
            }
        }
        composeTestRule.onNodeWithTag(TestTags.episodeCard(2)).performClick()
        assertEquals(2, clickedId)
    }

    // ── List not shown when empty ─────────────────────────────────────────────

    @Test
    fun emptyState_doesNotShowEpisodeList() {
        setScreen(EpisodeListUiState(episodes = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.EPISODE_LIST).assertDoesNotExist()
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private val threeEpisodes = listOf(
        Episode(id = 1, name = "Pilot", airDate = "December 2, 2013", episodeCode = "S01E01"),
        Episode(id = 2, name = "Lawnmower Dog", airDate = "December 9, 2013", episodeCode = "S01E02"),
        Episode(id = 3, name = "Anatomy Park", airDate = "December 16, 2013", episodeCode = "S01E03")
    )

    private fun setScreen(
        state: EpisodeListUiState,
        onAction: (EpisodeListAction) -> Unit = {},
        onEpisodeClick: (Int) -> Unit = {}
    ) {
        composeTestRule.setContent {
            RickAndMortyTheme {
                EpisodeListScreen(
                    uiState = state,
                    onAction = onAction,
                    onEpisodeClick = onEpisodeClick
                )
            }
        }
    }
}

