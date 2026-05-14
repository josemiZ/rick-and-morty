package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mickyzg.rickandmorty.presentation.component.CharacterCard
import com.mickyzg.rickandmorty.presentation.component.EmptyContent
import com.mickyzg.rickandmorty.presentation.component.LoadingIndicator
import com.mickyzg.rickandmorty.presentation.screen.preview.FavoritesPreviewProvider
import com.mickyzg.rickandmorty.presentation.screen.preview.ThemePreviews
import com.mickyzg.rickandmorty.presentation.viewmodel.favorites.FavoritesAction
import com.mickyzg.rickandmorty.presentation.viewmodel.favorites.FavoritesUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onAction: (FavoritesAction) -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Favorites") }) },
        modifier = modifier
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingIndicator(modifier = Modifier.fillMaxSize())
            uiState.favorites.isEmpty() -> EmptyContent(
                title = "No favorites yet",
                subtitle = "Tap the heart icon on any character to save it here."
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                items(items = uiState.favorites, key = { it.id }) { character ->
                    CharacterCard(
                        character = character,
                        onClick = { onCharacterClick(character.id) },
                        onFavoriteClick = { onAction(FavoritesAction.RemoveFavorite(character.id)) }
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun FavoritesScreenPreview(
    @PreviewParameter(FavoritesPreviewProvider::class) uiState: FavoritesUiState
) {
    RickAndMortyTheme {
        FavoritesScreen(
            uiState = uiState,
            onAction = {},
            onCharacterClick = {}
        )
    }
}
