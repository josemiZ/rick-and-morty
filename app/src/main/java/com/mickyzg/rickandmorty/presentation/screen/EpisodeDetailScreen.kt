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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.mickyzg.rickandmorty.presentation.component.EmptyContent
import com.mickyzg.rickandmorty.presentation.component.ErrorContent
import com.mickyzg.rickandmorty.presentation.component.LoadingIndicator
import com.mickyzg.rickandmorty.presentation.screen.preview.EpisodeDetailPreviewProvider
import com.mickyzg.rickandmorty.presentation.screen.preview.ThemePreviews
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail.EpisodeDetailAction
import com.mickyzg.rickandmorty.presentation.viewmodel.episodeDetail.EpisodeDetailUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailScreen(
    uiState: EpisodeDetailUiState,
    onAction: (EpisodeDetailAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val episode = uiState.episode

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(episode?.name ?: "Episode") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            uiState.isLoading && episode == null ->
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            uiState.error != null && episode == null ->
                ErrorContent(
                    message = uiState.error,
                    onRetry = { onAction(EpisodeDetailAction.Retry) }
                )
            episode != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .animateContentSize()
                ) {
                    Text(episode.name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        episode.episodeCode,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    EpisodeDetailRow("Air date", episode.airDate)
                    episode.characterUrls?.let { EpisodeDetailRow("Characters", it.size.toString()) }
                    if (uiState.isLoading) { Spacer(Modifier.height(8.dp)); LoadingIndicator() }
                }
            }
            else -> EmptyContent(title = "Episode not found")
        }
    }
}

@Composable
private fun EpisodeDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.5f))
    }
}

@ThemePreviews
@Composable
private fun EpisodeDetailScreenPreview(
    @PreviewParameter(EpisodeDetailPreviewProvider::class) uiState: EpisodeDetailUiState
) {
    RickAndMortyTheme {
        EpisodeDetailScreen(
            uiState = uiState,
            onAction = {},
            onBack = {}
        )
    }
}
