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
import com.mickyzg.rickandmorty.presentation.screen.preview.LocationDetailPreviewProvider
import com.mickyzg.rickandmorty.presentation.screen.preview.ThemePreviews
import com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail.LocationDetailAction
import com.mickyzg.rickandmorty.presentation.viewmodel.locationDetail.LocationDetailUiState
import com.mickyzg.rickandmorty.ui.theme.RickAndMortyTheme
import com.mickyzg.rickandmorty.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    uiState: LocationDetailUiState,
    onAction: (LocationDetailAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val location = uiState.location

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(location?.name ?: stringResource(R.string.title_location)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            uiState.isLoading && location == null ->
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            uiState.error != null && location == null ->
                ErrorContent(
                    message = uiState.error,
                    onRetry = { onAction(LocationDetailAction.Retry) }
                )
            location != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .animateContentSize()
                ) {
                    Text(location.name, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    if (location.type.isNotBlank()) LocationDetailRow(stringResource(R.string.detail_type), location.type)
                    if (location.dimension.isNotBlank()) LocationDetailRow(stringResource(R.string.detail_dimension), location.dimension)
                    location.residentUrls?.let { LocationDetailRow(stringResource(R.string.detail_residents), it.size.toString()) }
                    if (uiState.isLoading) { Spacer(Modifier.height(8.dp)); LoadingIndicator() }
                }
            }
            else -> EmptyContent(title = stringResource(R.string.empty_location_not_found))
        }
    }
}

@Composable
private fun LocationDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.5f))
    }
}

@ThemePreviews
@Composable
private fun LocationDetailScreenPreview(
    @PreviewParameter(LocationDetailPreviewProvider::class) uiState: LocationDetailUiState
) {
    RickAndMortyTheme {
        LocationDetailScreen(
            uiState = uiState,
            onAction = {},
            onBack = {}
        )
    }
}
