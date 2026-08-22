package subha.app.cyra.ui.profilesetup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import subha.app.cyra.R
import subha.app.cyra.ui.components.CyraAlertDialog
import subha.app.cyra.ui.components.CyraDateField
import subha.app.cyra.ui.components.CyraTextButton

/** This step is mandatory - see [subha.app.cyra.feature.profilesetup.presentation.ProfileSetupState.isPrimaryButtonEnabled]. */
@Composable
fun ProfileSetupLastPeriodStep(
    lastPeriodStartDate: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    onDontRememberConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDontRememberDialog by remember { mutableStateOf(false) }

    ProfileSetupStepScaffold(
        title = stringResource(R.string.profile_setup_last_period_title),
        subtitle = stringResource(R.string.profile_setup_last_period_subtitle),
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.profile_setup_last_period_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        CyraDateField(
            date = lastPeriodStartDate,
            placeholder = stringResource(R.string.profile_setup_last_period_placeholder),
            onDateSelected = onDateChange,
        )
        Spacer(modifier = Modifier.height(12.dp))
        // The mandatory requirement's escape hatch - confirming the reassurance dialog
        // satisfies it without an exact date (see ProfileSetupViewModel.onLastPeriodUnknownConfirmed).
        CyraTextButton(text = stringResource(R.string.profile_setup_last_period_dont_remember), onClick = { showDontRememberDialog = true })
    }

    if (showDontRememberDialog) {
        CyraAlertDialog(
            title = stringResource(R.string.profile_setup_last_period_dont_remember_title),
            message = stringResource(R.string.profile_setup_last_period_dont_remember_message),
            confirmText = stringResource(R.string.profile_setup_last_period_dont_remember_confirm),
            onConfirm = {
                showDontRememberDialog = false
                onDontRememberConfirmed()
            },
            dismissText = stringResource(R.string.profile_setup_last_period_dont_remember_cancel),
            onDismiss = { showDontRememberDialog = false },
        )
    }
}
