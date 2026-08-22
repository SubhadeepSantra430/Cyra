package subha.app.cyra.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import subha.app.cyra.R
import subha.app.cyra.ui.theme.CyraTheme

/**
 * The outlined "Continue with X" row used for both Google and Apple sign-in - one
 * composable, icon slot swapped per provider so a new provider never needs a new button
 * shape/style.
 */
@Composable
fun CyraSocialButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(20.dp)) { icon() }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun GoogleIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.ic_google_logo),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = modifier,
    )
}

@CyraPreviews
@Composable
private fun CyraSocialButtonPreview() {
    CyraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyraSocialButton(
                text = stringResource(R.string.auth_continue_with_google),
                icon = { GoogleIcon(modifier = Modifier.size(20.dp)) },
                onClick = {},
            )
        }
    }
}
