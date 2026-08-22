package subha.app.cyra.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * One sentence with a single embedded clickable, colored span - "Don't have an account?
 * **Sign up**" / "Already have an account? **Log in**". Uses Compose's
 * `LinkAnnotation.Clickable` text-link API rather than a `Row` of two separate `Text`s,
 * so the sentence still wraps as one unit on narrow screens.
 */
@Composable
fun CyraLinkText(prefix: String, linkText: String, onLinkClick: () -> Unit, modifier: Modifier = Modifier) {
    val linkStyle = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = MaterialTheme.typography.titleMedium.fontWeight)
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append(prefix)
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "link",
                styles = TextLinkStyles(style = linkStyle),
            ) { onLinkClick() },
        ) {
            append(linkText)
        }
    }
    Text(text = annotatedText, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = modifier)
}

@CyraPreviews
@Composable
private fun CyraLinkTextPreview() {
    CyraTheme {
        CyraLinkText(
            prefix = "Don't have an account? ",
            linkText = "Sign up",
            onLinkClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
