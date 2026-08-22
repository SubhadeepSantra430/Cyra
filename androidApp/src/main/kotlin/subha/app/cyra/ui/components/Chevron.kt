package subha.app.cyra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Which way the hand-drawn [Chevron] points. */
enum class ChevronDirection { Left, Right }

/**
 * Hand-drawn chevron via `Canvas`/`Path`, rather than pulling in an icon-font dependency
 * for one glyph. Extracted out of [CyraPrimaryButton] (its original home) so
 * [CyraBackButton] can reuse the same technique, mirrored, instead of duplicating it.
 */
@Composable
fun Chevron(color: Color, direction: ChevronDirection = ChevronDirection.Right, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            if (direction == ChevronDirection.Right) {
                moveTo(size.width * 0.25f, 0f)
                lineTo(size.width * 0.75f, size.height / 2f)
                lineTo(size.width * 0.25f, size.height)
            } else {
                moveTo(size.width * 0.75f, 0f)
                lineTo(size.width * 0.25f, size.height / 2f)
                lineTo(size.width * 0.75f, size.height)
            }
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = size.width * 0.18f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

/** Default size the chevron was originally drawn at inside [CyraPrimaryButton]. */
val ChevronDefaultSize = 14.dp
