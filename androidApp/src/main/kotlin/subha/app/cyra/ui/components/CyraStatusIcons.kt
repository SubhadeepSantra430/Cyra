package subha.app.cyra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * Hand-drawn tick/cross, same `Canvas`/`Path` technique as [Chevron] - used as the
 * leading icon on [CyraSnackbar] (success/error) and each row of
 * [PasswordRequirementsChecklist] (satisfied/unsatisfied).
 */
@Composable
fun TickIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.12f, size.height * 0.55f)
            lineTo(size.width * 0.42f, size.height * 0.85f)
            lineTo(size.width * 0.9f, size.height * 0.2f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = size.width * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
fun CrossIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.14f
        drawLine(
            color = color,
            start = Offset(size.width * 0.15f, size.height * 0.15f),
            end = Offset(size.width * 0.85f, size.height * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.85f, size.height * 0.15f),
            end = Offset(size.width * 0.15f, size.height * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@CyraPreviews
@Composable
private fun StatusIconsPreview() {
    CyraTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            TickIcon(color = Color(0xFF22C55E), modifier = Modifier.padding(end = 12.dp))
            CrossIcon(color = Color(0xFFEF4444))
        }
    }
}
