package subha.app.cyra.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * The one themed confirmation popup used app-wide - Manrope type, Cyra's rounded-corner
 * language, and [CyraPrimaryButton]/[CyraTextButton] (full-width, stacked - matching how
 * every other screen in the app presents a primary + secondary action) instead of
 * Material3's plain default `AlertDialog` look (side-by-side, intrinsic-width buttons).
 * Renders everything through the `text` slot with empty confirm/dismiss slots, since M3's
 * default button row isn't built for two full-width stacked buttons. Any screen needing
 * a "are you sure?" / reassurance popup uses this rather than building its own.
 */
@Composable
fun CyraAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(24.dp))
                CyraPrimaryButton(text = confirmText, onClick = onConfirm, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(4.dp))
                CyraTextButton(text = dismissText, onClick = onDismiss, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@CyraPreviews
@Composable
private fun CyraAlertDialogPreview() {
    subha.app.cyra.ui.theme.CyraTheme {
        CyraAlertDialog(
            title = "No worries!",
            message = "It's completely fine not to remember the exact date. We'll help you build an accurate picture over time.",
            confirmText = "OK, Continue",
            onConfirm = {},
            dismissText = "Cancel",
            onDismiss = {},
        )
    }
}
