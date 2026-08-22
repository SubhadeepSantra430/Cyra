package subha.app.cyra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * The one text field used across Auth (and any future form). Rounded-rect outline
 * matching the reference design (16dp corners, not the default Material stadium/sharp
 * shapes), leading icon slot, optional password mode with a hand-drawn eye/eye-off
 * toggle (no icon-font dependency, same convention as [Chevron]), optional error text.
 */
@Composable
fun CyraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    errorText: String? = null,
    keyboardType: KeyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
    enabled: Boolean = true,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyLarge) },
            leadingIcon = leadingIcon,
            trailingIcon = if (isPassword) {
                { EyeToggle(isVisible = isPasswordVisible, onClick = { onTogglePasswordVisibility?.invoke() }) }
            } else {
                null
            },
            isError = errorText != null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}

@Composable
private fun EyeToggle(isVisible: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Eye(isOpen = isVisible, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

/** Hand-drawn eye/eye-with-slash, matching [Chevron]'s "no icon-font dependency" convention. */
@Composable
private fun Eye(isOpen: Boolean, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val eyePath = Path().apply {
            moveTo(0f, size.height / 2f)
            quadraticTo(size.width / 2f, -size.height * 0.3f, size.width, size.height / 2f)
            quadraticTo(size.width / 2f, size.height * 1.3f, 0f, size.height / 2f)
            close()
        }
        drawPath(path = eyePath, color = color, style = Stroke(width = size.width * 0.08f, join = StrokeJoin.Round))
        drawCircle(color = color, radius = size.minDimension * 0.18f, center = center)
        if (!isOpen) {
            drawLine(
                color = color,
                start = Offset(size.width * 0.1f, size.height * 0.1f),
                end = Offset(size.width * 0.9f, size.height * 0.9f),
                strokeWidth = size.width * 0.1f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@CyraPreviews
@Composable
private fun CyraTextFieldPreview() {
    CyraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyraTextField(
                value = "",
                onValueChange = {},
                placeholder = "Email",
                leadingIcon = { EnvelopeIcon() },
            )
        }
    }
}

@CyraPreviews
@Composable
private fun CyraTextFieldPasswordPreview() {
    CyraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyraTextField(
                value = "hunter2",
                onValueChange = {},
                placeholder = "Password",
                leadingIcon = { LockIcon() },
                isPassword = true,
                errorText = "Password must be at least 8 characters",
            )
        }
    }
}

/** Hand-drawn envelope icon for email fields - see [Chevron]'s doc comment for why this isn't an icon font. */
@Composable
fun EnvelopeIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.09f
        drawRoundRect(
            color = color,
            size = size,
            cornerRadius = CornerRadius(size.width * 0.12f),
            style = Stroke(width = strokeWidth),
        )
        val flapPath = Path().apply {
            moveTo(size.width * 0.08f, size.height * 0.18f)
            lineTo(size.width / 2f, size.height * 0.58f)
            lineTo(size.width * 0.92f, size.height * 0.18f)
        }
        drawPath(path = flapPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Hand-drawn padlock icon for password fields. */
@Composable
fun LockIcon(color: Color = MaterialTheme.colorScheme.onSurfaceVariant, modifier: Modifier = Modifier.size(20.dp)) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.1f
        val bodyTop = size.height * 0.45f
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, bodyTop),
            size = Size(size.width, size.height - bodyTop),
            cornerRadius = CornerRadius(size.width * 0.15f),
            style = Stroke(width = strokeWidth),
        )
        val shacklePath = Path().apply {
            val left = size.width * 0.2f
            val right = size.width * 0.8f
            val top = 0f
            moveTo(left, bodyTop)
            lineTo(left, size.height * 0.25f)
            quadraticTo(left, top, size.width / 2f, top)
            quadraticTo(right, top, right, size.height * 0.25f)
            lineTo(right, bodyTop)
        }
        drawPath(path = shacklePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
