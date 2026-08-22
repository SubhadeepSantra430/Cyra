package subha.app.cyra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * The header shared by every step of the profile-setup flow (and any future multi-step
 * form): an optional [CyraBackButton] on the left, a plain-text category title centered
 * between it and the step counter (no chip/pill background - a first draft used one and
 * it read as a badge rather than a title), and a segmented (one bar per step) progress
 * indicator underneath, not a single continuous bar - each completed/current step reads
 * as its own filled segment, matching the reference design more closely than a smooth
 * fill does.
 *
 * [category]/[onBackClick] are both nullable - the first step in a flow typically hides
 * the back button (nothing to go back to), and a completion screen typically has no
 * category to show.
 */
@Composable
fun CyraCategoryStepHeader(
    stepNumber: Int,
    totalSteps: Int,
    category: String?,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.height(40.dp)) {
                if (onBackClick != null) {
                    CyraBackButton(onClick = onBackClick)
                }
            }
            Text(
                text = category.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "$stepNumber of $totalSteps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(totalSteps) { index ->
                val completedOrCurrent = index < stepNumber
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            color = if (completedOrCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(2.dp),
                        ),
                )
            }
        }
    }
}

@CyraPreviews
@Composable
private fun CyraCategoryStepHeaderPreview() {
    CyraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyraCategoryStepHeader(stepNumber = 3, totalSteps = 8, category = "About You", onBackClick = {})
        }
    }
}
