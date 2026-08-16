package subha.app.cyra.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * The plain-text "skip"/secondary action button, matching the onboarding reference
 * design (purple text, no background). Used anywhere a low-emphasis text action is
 * needed alongside a [CyraPrimaryButton].
 */
@Composable
fun CyraTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CyraTextButtonPreview() {
    CyraTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            CyraTextButton(text = "Skip", onClick = {})
        }
    }
}
