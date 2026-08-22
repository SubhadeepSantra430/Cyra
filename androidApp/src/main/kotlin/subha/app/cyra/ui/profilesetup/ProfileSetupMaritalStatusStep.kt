package subha.app.cyra.ui.profilesetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import subha.app.cyra.R
import subha.app.cyra.feature.profilesetup.domain.MaritalStatus
import subha.app.cyra.ui.components.BrokenHeartIcon
import subha.app.cyra.ui.components.CyraRadioOptionRow
import subha.app.cyra.ui.components.HeartIcon
import subha.app.cyra.ui.components.LockIcon
import subha.app.cyra.ui.components.PersonIcon
import subha.app.cyra.ui.components.RingsIcon

@Composable
fun ProfileSetupMaritalStatusStep(
    selected: MaritalStatus?,
    onSelected: (MaritalStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSetupStepScaffold(
        title = stringResource(R.string.profile_setup_marital_status_title),
        subtitle = stringResource(R.string.profile_setup_marital_status_subtitle),
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.profile_setup_marital_status_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MaritalStatus.entries.forEach { status ->
                CyraRadioOptionRow(
                    icon = { MaritalStatusIcon(status) },
                    label = stringResource(profileSetupMessageKeyToStringRes(status.messageKey)),
                    selected = selected == status,
                    onClick = { onSelected(status) },
                )
            }
        }
    }
}

@Composable
private fun MaritalStatusIcon(status: MaritalStatus) {
    when (status) {
        MaritalStatus.Single -> HeartIcon()
        MaritalStatus.Married -> RingsIcon()
        MaritalStatus.Divorced -> BrokenHeartIcon()
        MaritalStatus.Widowed -> PersonIcon()
        MaritalStatus.PreferNotToSay -> LockIcon()
    }
}
