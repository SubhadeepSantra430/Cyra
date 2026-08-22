package subha.app.cyra.ui.profilesetup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import subha.app.cyra.R
import subha.app.cyra.ui.components.CyraDateField

@Composable
fun ProfileSetupBirthdayStep(
    dateOfBirth: LocalDate?,
    dateOfBirthError: String?,
    onDateOfBirthChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSetupStepScaffold(
        title = stringResource(R.string.profile_setup_birthday_title),
        subtitle = stringResource(R.string.profile_setup_birthday_subtitle),
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.profile_setup_birthday_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        CyraDateField(
            date = dateOfBirth,
            placeholder = stringResource(R.string.profile_setup_birthday_placeholder),
            onDateSelected = onDateOfBirthChange,
            errorText = dateOfBirthError?.let { stringResource(profileSetupMessageKeyToStringRes(it)) },
        )
    }
}
