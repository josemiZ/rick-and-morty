package com.mickyzg.rickandmorty.presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mickyzg.rickandmorty.domain.model.Character
import com.mickyzg.rickandmorty.R

/**
 * Card representing a single [Character] in the list or favorites screens.
 *
 * Displays a circular avatar (loaded via Coil), name, species, status chip and a
 * favorite toggle button. Uses [animateContentSize] to smoothly adjust layout when
 * the card content changes.
 */
@Composable
fun CharacterCard(
    character: Character,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .animateContentSize()
            .testTag(TestTags.characterCard(character.id)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = character.species,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusChip(
                    status = character.status,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(onClick = onFavoriteClick, modifier = Modifier.testTag(TestTags.favoriteButton(character.id))) {
                Icon(
                    imageVector = if (character.isFavorite) Icons.Filled.Favorite
                    else Icons.Filled.FavoriteBorder,
                    contentDescription = if (character.isFavorite) stringResource(R.string.cd_remove_from_favorites)
                    else stringResource(R.string.cd_add_to_favorites),
                    tint = if (character.isFavorite) Color(0xFFF44336)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


