package com.mickyzg.rickandmorty.presentation.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mickyzg.rickandmorty.presentation.component.ErrorContent
import com.mickyzg.rickandmorty.presentation.component.LoadingIndicator
import com.mickyzg.rickandmorty.presentation.component.StatusChip
import com.mickyzg.rickandmorty.presentation.screen.preview.CharacterDetailPreviewProvider
import com.mickyzg.rickandmorty.presentation.screen.preview.ThemePreviews
import com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail.CharacterDetailAction
import com.mickyzg.rickandmorty.presentation.viewmodel.characterDetail.CharacterDetailUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onAction: (CharacterDetailAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val character = uiState.character

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character?.name ?: "Character") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (character != null) {
                        IconButton(onClick = { onAction(CharacterDetailAction.ToggleFavorite) }) {
                            Icon(
                                imageVector = if (character.isFavorite) Icons.Filled.Favorite
                                else Icons.Filled.FavoriteBorder,
                                contentDescription = "Toggle favorite",
                                tint = if (character.isFavorite) Color(0xFFF44336)
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            uiState.isLoading && character == null ->
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            uiState.error != null && character == null ->
                ErrorContent(
                    message = uiState.error,
                    onRetry = { onAction(CharacterDetailAction.Retry) }
                )
            character != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .animateContentSize()
                ) {
                    AsyncImage(
                        model = character.imageUrl,
                        contentDescription = character.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(280.dp)
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(character.name, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(4.dp))
                        StatusChip(character.status)
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        DetailRow("Species", character.species)
                        character.gender?.let { DetailRow("Gender", it.name.lowercase().replaceFirstChar { c -> c.uppercase() }) }
                        character.type?.let { if (it.isNotBlank()) DetailRow("Type", it) }
                        character.origin?.let { DetailRow("Origin", it.name) }
                        character.location?.let { DetailRow("Last known location", it.name) }
                        character.episodeUrls?.let { DetailRow("Episodes", it.size.toString()) }
                        if (uiState.isLoading) { Spacer(Modifier.height(8.dp)); LoadingIndicator() }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.5f))
    }
}

@ThemePreviews
@Composable
private fun CharacterDetailScreenPreview(
    @PreviewParameter(CharacterDetailPreviewProvider::class) uiState: CharacterDetailUiState
) {
    RickAndMortyTheme {
        CharacterDetailScreen(
            uiState = uiState,
            onAction = {},
            onBack = {}
        )
    }
}
