package subha.app.cyra.ui.profilesetup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import subha.app.cyra.R
import subha.app.cyra.ui.components.CyraTextField
import subha.app.cyra.ui.components.PersonIcon

@Composable
fun ProfileSetupNameStep(
    name: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSetupStepScaffold(
        title = stringResource(R.string.profile_setup_name_title),
        subtitle = stringResource(R.string.profile_setup_name_subtitle),
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.profile_setup_name_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        CyraTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = stringResource(R.string.profile_setup_name_placeholder),
            leadingIcon = { PersonIcon() },
            errorText = nameError?.let { stringResource(profileSetupMessageKeyToStringRes(it)) },
        )
    }
}
