package subha.app.cyra.ui.profilesetup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import subha.app.cyra.R
import subha.app.cyra.feature.profilesetup.domain.CycleRegularity
import subha.app.cyra.ui.components.CyraSegmentedToggle
import subha.app.cyra.ui.components.CyraTextField

@Composable
fun ProfileSetupCycleInfoStep(
    averageCycleLengthDays: String,
    averagePeriodDurationDays: String,
    cycleRegularity: CycleRegularity,
    onCycleLengthChange: (String) -> Unit,
    onPeriodDurationChange: (String) -> Unit,
    onCycleRegularityChange: (CycleRegularity) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSetupStepScaffold(
        title = stringResource(R.string.profile_setup_cycle_info_title),
        subtitle = stringResource(R.string.profile_setup_cycle_info_subtitle),
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.profile_setup_cycle_length_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        CyraTextField(
            value = averageCycleLengthDays,
            onValueChange = onCycleLengthChange,
            placeholder = "28",
            leadingIcon = null,
            trailingLabel = stringResource(R.string.profile_setup_days_suffix),
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(R.string.profile_setup_period_duration_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        CyraTextField(
            value = averagePeriodDurationDays,
            onValueChange = onPeriodDurationChange,
            placeholder = "5",
            leadingIcon = null,
            trailingLabel = stringResource(R.string.profile_setup_days_suffix),
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(R.string.profile_setup_cycle_regularity_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        val options = CycleRegularity.entries
        CyraSegmentedToggle(
            options = options.map { stringResource(profileSetupMessageKeyToStringRes(it.messageKey)) },
            selectedIndex = options.indexOf(cycleRegularity),
            onOptionSelected = { index -> onCycleRegularityChange(options[index]) },
        )
    }
}
