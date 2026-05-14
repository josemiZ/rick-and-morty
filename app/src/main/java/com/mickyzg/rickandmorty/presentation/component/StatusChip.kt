package com.mickyzg.rickandmorty.presentation.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mickyzg.rickandmorty.domain.model.CharacterStatus

private val AliveColor = Color(0xFF4CAF50)
private val DeadColor = Color(0xFFF44336)

/**
 * A small colored chip that displays the [CharacterStatus] of a character.
 *
 * Green = Alive, Red = Dead, Gray (surface variant) = Unknown.
 */
@Composable
fun StatusChip(
    status: CharacterStatus,
    modifier: Modifier = Modifier
) {
    val (containerColor, label) = when (status) {
        CharacterStatus.ALIVE -> AliveColor.copy(alpha = 0.15f) to "Alive"
        CharacterStatus.DEAD -> DeadColor.copy(alpha = 0.15f) to "Dead"
        CharacterStatus.UNKNOWN -> MaterialTheme.colorScheme.surfaceVariant to "Unknown"
    }
    val textColor = when (status) {
        CharacterStatus.ALIVE -> AliveColor
        CharacterStatus.DEAD -> DeadColor
        CharacterStatus.UNKNOWN -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    AssistChip(
        onClick = {},
        label = { Text(text = label, color = textColor, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(containerColor = containerColor),
        border = AssistChipDefaults.assistChipBorder(enabled = false)
    )
}

