package com.mickyzg.rickandmorty.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** Centered loading indicator — used as the last item in a lazy list when paginating. */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(TestTags.LOADING_INDICATOR),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

