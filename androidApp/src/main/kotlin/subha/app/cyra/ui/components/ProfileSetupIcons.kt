package subha.app.cyra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * Hand-drawn icons for the profile-setup flow - same `Canvas`/`Path` technique as
 * [EnvelopeIcon]/[LockIcon] (see [Chevron]'s doc comment for why this isn't an icon
 * font). [PersonIcon] is used both for the name field's leading icon and for
 * [subha.app.cyra.feature.profilesetup.domain.MaritalStatus.Widowed]'s row icon (a lone
 * figure); [LockIcon] (already defined for password fields) is reused as-is for
 * [subha.app.cyra.feature.profilesetup.domain.MaritalStatus.PreferNotToSay].
 */
@Composable
fun PersonIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.11f
        drawCircle(color = color, radius = size.width * 0.19f, center = Offset(size.width / 2f, size.height * 0.28f), style = Stroke(width = strokeWidth))
        val bodyPath = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.92f)
            quadraticTo(size.width * 0.15f, size.height * 0.55f, size.width / 2f, size.height * 0.55f)
            quadraticTo(size.width * 0.85f, size.height * 0.55f, size.width * 0.85f, size.height * 0.92f)
        }
        drawPath(path = bodyPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

/** Leading icon for the height field. */
@Composable
fun RulerIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.09f
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, size.height * 0.3f),
            size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.45f),
            cornerRadius = CornerRadius(size.width * 0.08f),
            style = Stroke(width = strokeWidth),
        )
        listOf(0.25f, 0.45f, 0.65f, 0.85f).forEach { fraction ->
            drawLine(
                color = color,
                start = Offset(size.width * fraction, size.height * 0.3f),
                end = Offset(size.width * fraction, size.height * 0.5f),
                strokeWidth = strokeWidth * 0.7f,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** Leading icon for the weight field. */
@Composable
fun ScaleIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.09f
        drawCircle(color = color, radius = size.width * 0.46f, center = center, style = Stroke(width = strokeWidth))
        val needlePath = Path().apply {
            moveTo(size.width / 2f, size.height / 2f)
            lineTo(size.width * 0.72f, size.height * 0.28f)
        }
        drawPath(path = needlePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

/** Leading icon for date fields (birthday, last period start date). */
@Composable
fun CalendarIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.09f
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, size.height * 0.18f),
            size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.82f),
            cornerRadius = CornerRadius(size.width * 0.14f),
            style = Stroke(width = strokeWidth),
        )
        drawLine(color = color, start = Offset(0f, size.height * 0.42f), end = Offset(size.width, size.height * 0.42f), strokeWidth = strokeWidth)
        listOf(0.28f, 0.72f).forEach { fraction ->
            drawLine(
                color = color,
                start = Offset(size.width * fraction, 0f),
                end = Offset(size.width * fraction, size.height * 0.3f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}

/** [subha.app.cyra.feature.profilesetup.domain.MaritalStatus.Single] row icon. */
@Composable
fun HeartIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = heartPath(size.width, size.height)
        drawPath(path = path, color = color, style = Stroke(width = size.width * 0.09f, join = StrokeJoin.Round))
    }
}

/** [subha.app.cyra.feature.profilesetup.domain.MaritalStatus.Married] row icon - two interlocked rings. */
@Composable
fun RingsIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.1f
        val radius = size.width * 0.24f
        drawCircle(color = color, radius = radius, center = Offset(size.width * 0.38f, size.height * 0.62f), style = Stroke(width = strokeWidth))
        drawCircle(color = color, radius = radius, center = Offset(size.width * 0.65f, size.height * 0.62f), style = Stroke(width = strokeWidth))
    }
}

/** [subha.app.cyra.feature.profilesetup.domain.MaritalStatus.Divorced] row icon - a heart with a break through it. */
@Composable
fun BrokenHeartIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = heartPath(size.width, size.height)
        drawPath(path = path, color = color, style = Stroke(width = size.width * 0.08f, join = StrokeJoin.Round))
        val crackPath = Path().apply {
            moveTo(size.width * 0.55f, size.height * 0.32f)
            lineTo(size.width * 0.38f, size.height * 0.55f)
            lineTo(size.width * 0.58f, size.height * 0.68f)
            lineTo(size.width * 0.45f, size.height * 0.9f)
        }
        drawPath(path = crackPath, color = color, style = Stroke(width = size.width * 0.06f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

private fun heartPath(width: Float, height: Float): Path = Path().apply {
    moveTo(width / 2f, height * 0.88f)
    cubicTo(width * 0.05f, height * 0.55f, width * 0.05f, height * 0.18f, width / 2f, height * 0.32f)
    cubicTo(width * 0.95f, height * 0.18f, width * 0.95f, height * 0.55f, width / 2f, height * 0.88f)
    close()
}

@CyraPreviews
@Composable
private fun ProfileSetupIconsPreview() {
    CyraTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            PersonIcon(modifier = Modifier.padding(end = 8.dp))
            RulerIcon(modifier = Modifier.padding(end = 8.dp))
            ScaleIcon(modifier = Modifier.padding(end = 8.dp))
            CalendarIcon(modifier = Modifier.padding(end = 8.dp))
            HeartIcon(modifier = Modifier.padding(end = 8.dp))
            RingsIcon(modifier = Modifier.padding(end = 8.dp))
            BrokenHeartIcon()
        }
    }
}
