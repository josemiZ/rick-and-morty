package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mickyzg.rickandmorty.presentation.component.TestTags
import com.mickyzg.rickandmorty.presentation.viewmodel.favorites.FavoritesAction
import com.mickyzg.rickandmorty.presentation.viewmodel.favorites.FavoritesUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import com.mickyzg.rickandmorty.util.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Empty state ───────────────────────────────────────────────────────────

    @Test
    fun emptyState_showsEmptyContent() {
        setScreen(FavoritesUiState(favorites = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertIsDisplayed()
    }

    @Test
    fun emptyState_showsNoFavoritesYetText() {
        setScreen(FavoritesUiState(favorites = emptyList()))
        composeTestRule.onNodeWithText("No favorites yet").assertIsDisplayed()
    }

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun loadingState_doesNotShowListOrEmpty() {
        setScreen(FavoritesUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.FAVORITES_LIST).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_CONTENT).assertDoesNotExist()
    }

    // ── Favorites list ────────────────────────────────────────────────────────

    @Test
    fun favoritesList_rendersAllCards() {
        setScreen(FavoritesUiState(favorites = TestFixtures.twoFavorites))
        composeTestRule.onNodeWithTag(TestTags.FAVORITES_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.characterCard(1)).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.characterCard(2)).assertIsDisplayed()
    }

    @Test
    fun favoritesList_displaysCharacterNames() {
        setScreen(FavoritesUiState(favorites = TestFixtures.twoFavorites))
        composeTestRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeTestRule.onNodeWithText("Morty Smith").assertIsDisplayed()
    }

    // ── Remove favorite action ────────────────────────────────────────────────

    @Test
    fun favoriteButton_clickFiresRemoveFavoriteAction() {
        val actions = mutableListOf<FavoritesAction>()
        setScreen(
            state = FavoritesUiState(favorites = TestFixtures.twoFavorites),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithTag(TestTags.favoriteButton(1)).performClick()
        val removeActions = actions.filterIsInstance<FavoritesAction.RemoveFavorite>()
        assertEquals(1, removeActions.size)
        assertEquals(1, removeActions[0].characterId)
    }

    @Test
    fun favoriteButton_clickOnSecondCard_firesCorrectId() {
        val actions = mutableListOf<FavoritesAction>()
        setScreen(
            state = FavoritesUiState(favorites = TestFixtures.twoFavorites),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithTag(TestTags.favoriteButton(2)).performClick()
        val removeActions = actions.filterIsInstance<FavoritesAction.RemoveFavorite>()
        assertEquals(1, removeActions.size)
        assertEquals(2, removeActions[0].characterId)
    }

    // ── Character card click ──────────────────────────────────────────────────

    @Test
    fun characterCard_clickFiresOnCharacterClickCallback() {
        var clickedId: Int? = null
        composeTestRule.setContent {
            RickAndMortyTheme {
                FavoritesScreen(
                    uiState = FavoritesUiState(favorites = TestFixtures.twoFavorites),
                    onAction = {},
                    onCharacterClick = { clickedId = it }
                )
            }
        }
        composeTestRule.onNodeWithTag(TestTags.characterCard(2)).performClick()
        assertEquals(2, clickedId)
    }

    // ── List not shown when empty ─────────────────────────────────────────────

    @Test
    fun emptyState_doesNotShowFavoritesList() {
        setScreen(FavoritesUiState(favorites = emptyList()))
        composeTestRule.onNodeWithTag(TestTags.FAVORITES_LIST).assertDoesNotExist()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun setScreen(
        state: FavoritesUiState,
        onAction: (FavoritesAction) -> Unit = {},
        onCharacterClick: (Int) -> Unit = {}
    ) {
        composeTestRule.setContent {
            RickAndMortyTheme {
                FavoritesScreen(
                    uiState = state,
                    onAction = onAction,
                    onCharacterClick = onCharacterClick
                )
            }
        }
    }
}

