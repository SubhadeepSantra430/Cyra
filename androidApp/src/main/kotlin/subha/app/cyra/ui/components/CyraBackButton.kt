package subha.app.cyra.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import subha.app.cyra.R
import subha.app.cyra.ui.theme.CyraTheme

/**
 * The floating circular back button from the reference design (white/surface circle,
 * subtle elevation, left chevron). No back-button convention existed anywhere in the
 * app before Auth - this is the first one, built for reuse by any future screen.
 */
@Composable
fun CyraBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.auth_back_content_description)
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .semantics { contentDescription = description },
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Chevron(
            color = MaterialTheme.colorScheme.onSurface,
            direction = ChevronDirection.Left,
            modifier = Modifier.size(ChevronDefaultSize),
        )
    }
}

@CyraPreviews
@Composable
private fun CyraBackButtonPreview() {
    CyraTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            CyraBackButton(onClick = {})
        }
    }
}
