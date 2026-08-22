package subha.app.cyra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * A round checkbox, matching the reference design's circular Terms-agreement toggle -
 * Material's default `Checkbox` is a square/rounded-square, so this is a small purpose-
 * built replacement rather than a style override.
 */
@Composable
fun CyraCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val borderColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val fillColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .size(22.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!checked) },
            )
            .background(color = fillColor, shape = CircleShape)
            .border(width = 1.5.dp, color = borderColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Canvas(modifier = Modifier.size(12.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.12f, size.height * 0.55f)
                    lineTo(size.width * 0.42f, size.height * 0.85f)
                    lineTo(size.width * 0.9f, size.height * 0.2f)
                }
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = size.width * 0.16f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
    }
}

@CyraPreviews
@Composable
private fun CyraCheckboxPreview() {
    CyraTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            CyraCheckbox(checked = false, onCheckedChange = {})
            Spacer(modifier = Modifier.size(12.dp))
            CyraCheckbox(checked = true, onCheckedChange = {})
        }
    }
}
