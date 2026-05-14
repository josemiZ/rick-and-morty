package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.domain.model.CharacterGender
import com.mickyzg.rickandmorty.domain.model.CharacterLocation
import com.mickyzg.rickandmorty.domain.model.CharacterStatus
import com.mickyzg.rickandmorty.presentation.component.TestTags
import com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail.CharacterDetailAction
import com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail.CharacterDetailUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CharacterDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Loading state ─────────────────────────────────────────────────────────

    @Test
    fun loadingState_showsLoadingIndicator() {
        setScreen(CharacterDetailUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotShowErrorContent() {
        setScreen(CharacterDetailUiState(isLoading = true))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertDoesNotExist()
    }

    // ── Error state ───────────────────────────────────────────────────────────

    @Test
    fun errorState_showsErrorContentWithMessage() {
        setScreen(CharacterDetailUiState(error = "Connection failed"))
        composeTestRule.onNodeWithTag(TestTags.ERROR_CONTENT).assertIsDisplayed()
        composeTestRule.onNodeWithText("Connection failed").assertIsDisplayed()
    }

    @Test
    fun errorState_retryButtonFiresRetryAction() {
        val actions = mutableListOf<CharacterDetailAction>()
        setScreen(
            state = CharacterDetailUiState(error = "Timeout"),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(actions.contains(CharacterDetailAction.Retry))
    }

    // ── Character detail content ──────────────────────────────────────────────

    @Test
    fun characterLoaded_displaysName() {
        setScreen(CharacterDetailUiState(character = detailCharacter))
        composeTestRule.onAllNodesWithText("Rick Sanchez")[0].assertIsDisplayed()
    }

    @Test
    fun characterLoaded_displaysSpecies() {
        setScreen(CharacterDetailUiState(character = detailCharacter))
        composeTestRule.onNodeWithText("Species").assertIsDisplayed()
        composeTestRule.onNodeWithText("Human").assertIsDisplayed()
    }

    @Test
    fun characterLoaded_displaysGender() {
        setScreen(CharacterDetailUiState(character = detailCharacter))
        composeTestRule.onNodeWithText("Gender").assertIsDisplayed()
        composeTestRule.onNodeWithText("Male").assertIsDisplayed()
    }

    @Test
    fun characterLoaded_displaysOrigin() {
        setScreen(CharacterDetailUiState(character = detailCharacter))
        composeTestRule.onNodeWithText("Origin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Earth (C-137)").assertIsDisplayed()
    }

    @Test
    fun characterLoaded_displaysLastKnownLocation() {
        setScreen(CharacterDetailUiState(character = detailCharacter))
        composeTestRule.onNodeWithText("Last known location").assertIsDisplayed()
        composeTestRule.onNodeWithText("Citadel of Ricks").assertIsDisplayed()
    }

    @Test
    fun characterLoaded_displaysEpisodeCount() {
        setScreen(CharacterDetailUiState(character = detailCharacter))
        composeTestRule.onNodeWithText("Episodes").assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }

    // ── Favorite toggle ───────────────────────────────────────────────────────

    @Test
    fun toggleFavoriteButton_firesToggleFavoriteAction() {
        val actions = mutableListOf<CharacterDetailAction>()
        setScreen(
            state = CharacterDetailUiState(character = detailCharacter),
            onAction = { actions.add(it) }
        )
        composeTestRule.onNodeWithContentDescription("Toggle favorite").performClick()
        assertEquals(1, actions.size)
        assertEquals(CharacterDetailAction.ToggleFavorite, actions[0])
    }

    // ── Back button ───────────────────────────────────────────────────────────

    @Test
    fun backButton_firesOnBackCallback() {
        var backPressed = false
        composeTestRule.setContent {
            RickAndMortyTheme {
                CharacterDetailScreen(
                    uiState = CharacterDetailUiState(character = detailCharacter),
                    onAction = {},
                    onBack = { backPressed = true }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(backPressed)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private val detailCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.ALIVE,
        species = "Human",
        imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        isFavorite = false,
        gender = CharacterGender.MALE,
        origin = CharacterLocation(name = "Earth (C-137)", url = "https://loc/1"),
        location = CharacterLocation(name = "Citadel of Ricks", url = "https://loc/3"),
        episodeUrls = listOf("https://ep/1", "https://ep/2", "https://ep/3"),
        isDetailLoaded = true
    )

    private fun setScreen(
        state: CharacterDetailUiState,
        onAction: (CharacterDetailAction) -> Unit = {},
        onBack: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            RickAndMortyTheme {
                CharacterDetailScreen(
                    uiState = state,
                    onAction = onAction,
                    onBack = onBack
                )
            }
        }
    }
}

