package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.mickyzg.rickandmorty.presentation.component.TestTags
import com.mickyzg.rickandmorty.presentation.viewmodel.characterList.CharacterListAction
import com.mickyzg.rickandmorty.presentation.viewmodel.characterList.CharacterListUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import com.mickyzg.rickandmorty.util.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CharacterListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsEmptyContent() {
        setScreen(CharacterListUiState(characters = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertIsDisplayed()
    }

    @Test
    fun emptyState_showsNoCharactersFoundText() {
        setScreen(CharacterListUiState(characters = emptyList()))
        composeTestRule.onNodeWithText("No characters found").assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotShowListOrEmptyOrError() {
        setScreen(CharacterListUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.CHARACTER_LIST).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertDoesNotExist()
    }

    @Test
    fun errorState_showsErrorContentWithMessage() {
        setScreen(CharacterListUiState(error = "Network unavailable", characters = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Network unavailable").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButtonFiresRetryAction() {
        val actions = mutableListOf<CharacterListAction>()
        setScreen(
            state = CharacterListUiState(error = "Timeout", characters = emptyList()),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(actions.contains(CharacterListAction.Retry))
    }

    @Test
    fun characterList_rendersAllCards() {
        setScreen(CharacterListUiState(characters = TestFixtures.threeCharacters))
        composeTestRule.onNodeWithTag(TestTags.CHARACTER_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.characterCard(1)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.characterCard(2)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.characterCard(3)).assertIsDisplayed()
    }

    @Test
    fun characterList_displaysCharacterNames() {
        setScreen(CharacterListUiState(characters = TestFixtures.threeCharacters))
        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morty Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("Summer Smith").assertIsDisplayed()
    }

    @Test
    fun searchField_typingText_firesSearchQueryChangedAction() {
        val actions = mutableListOf<CharacterListAction>()
        setScreen(
            state = CharacterListUiState(characters = TestFixtures.threeCharacters),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithTag(TestTags.SEARCH_FIELD).performTextReplacement("Rick")
        val searchActions = actions.filterIsInstance<CharacterListAction.SearchQueryChanged>()
        assertTrue(searchActions.isNotEmpty())
        assertEquals("Rick", searchActions.last().query)
    }

    @Test
    fun searchField_clearButton_firesEmptySearchQueryAction() {
        val actions = mutableListOf<CharacterListAction>()
        setScreen(
            state = CharacterListUiState(characters = TestFixtures.threeCharacters, searchQuery = "Rick"),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        val clearActions = actions.filterIsInstance<CharacterListAction.SearchQueryChanged>()
        assertTrue(clearActions.any { it.query == "" })
    }

    @Test
    fun favoriteButton_clickFiresToggleFavoriteAction() {
        val actions = mutableListOf<CharacterListAction>()
        setScreen(
            state = CharacterListUiState(characters = TestFixtures.threeCharacters),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithTag(TestTags.favoriteButton(1)).performClick()
        val favActions = actions.filterIsInstance<CharacterListAction.ToggleFavorite>()
        assertEquals(1, favActions.size)
        assertEquals(1, favActions[0].characterId)
        assertEquals(true, favActions[0].isFavorite)
    }

    @Test
    fun characterCard_clickFiresOnCharacterClickCallback() {
        var clickedId: Int? = null
        composeTestRule.setContent {
            RickAndMortyTheme {
                CharacterListScreen(
                    uiState = CharacterListUiState(characters = TestFixtures.threeCharacters),
                    onAction = {},
                    onCharacterClick = { clickedId = it }
                )
            }
        }
        composeTestRule.onNodeWithTag(TestTags.characterCard(2)).performClick()
        assertEquals(2, clickedId)
    }

    private fun setScreen(
        state: CharacterListUiState,
        onAction: (CharacterListAction) -> Unit = {},
        onCharacterClick: (Int) -> Unit = {}
    ) {
        composeTestRule.setContent {
            RickAndMortyTheme {
                CharacterListScreen(
                    uiState = state,
                    onAction = onAction,
                    onCharacterClick = onCharacterClick
                )
            }
        }
    }
}
