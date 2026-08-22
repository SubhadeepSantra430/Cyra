package subha.app.cyra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * A 2-4 option switch - the height/weight unit toggle (cm/ft-in, kg/lb) and the "How is
 * your cycle?" 3-way choice (Regular/Irregular/Not sure) are both this same component,
 * just with a different [options] list. Not a Boolean-only toggle - stores the selected
 * index so a third option (cycle regularity) needs no separate component.
 *
 * Each option is its own pill: unselected options show an outline only (no fill), the
 * selected option shows a solid fill (no border) - a first draft had unselected options
 * as plain, borderless text, which didn't read as tappable.
 */
@Composable
fun CyraSegmentedToggle(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onOptionSelected(index) },
                    )
                    .then(
                        if (selected) {
                            Modifier.background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp))
                        } else {
                            Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(10.dp))
                        },
                    )
                    .padding(vertical = 10.dp, horizontal = 12.dp),
            )
        }
    }
}

@CyraPreviews
@Composable
private fun CyraSegmentedTogglePreview() {
    CyraTheme {
        CyraSegmentedToggle(options = listOf("Regular", "Irregular", "Not sure"), selectedIndex = 0, onOptionSelected = {})
    }
}
