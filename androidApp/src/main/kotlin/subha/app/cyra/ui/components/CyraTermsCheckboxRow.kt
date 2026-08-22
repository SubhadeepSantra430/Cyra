package subha.app.cyra.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import subha.app.cyra.R
import subha.app.cyra.ui.theme.CyraTheme

/**
 * Checkbox + "I agree to the **Terms of Service** and **Privacy Policy**" - the two
 * bolded spans are independently clickable, the rest is plain gray text.
 */
@Composable
fun CyraTermsCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val linkStyle = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = MaterialTheme.typography.titleMedium.fontWeight)
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append(stringResource(R.string.auth_terms_prefix))
        }
        withLink(LinkAnnotation.Clickable(tag = "terms", styles = TextLinkStyles(style = linkStyle)) { onTermsClick() }) {
            append(stringResource(R.string.auth_terms_link))
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append(stringResource(R.string.auth_terms_middle))
        }
        withLink(LinkAnnotation.Clickable(tag = "privacy", styles = TextLinkStyles(style = linkStyle)) { onPrivacyClick() }) {
            append(stringResource(R.string.auth_privacy_link))
        }
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        CyraCheckbox(checked = checked, onCheckedChange = onCheckedChange, modifier = Modifier.padding(top = 2.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@CyraPreviews
@Composable
private fun CyraTermsCheckboxRowPreview() {
    CyraTheme {
        CyraTermsCheckboxRow(
            checked = false,
            onCheckedChange = {},
            onTermsClick = {},
            onPrivacyClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
