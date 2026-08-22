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
import subha.app.cyra.ui.components.CyraTextButton

@Composable
fun ProfileSetupLastPeriodStep(
    lastPeriodStartDate: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    onDontRememberClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
        // Functionally identical to the bottom "Skip" button (advances without setting a
        // date) - kept as a separate, more specific-sounding affordance right next to
        // the field, matching the reference design.
        CyraTextButton(text = stringResource(R.string.profile_setup_last_period_dont_remember), onClick = onDontRememberClick)
    }
}
