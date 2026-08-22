package subha.app.cyra.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import subha.app.cyra.feature.auth.domain.AuthValidators
import subha.app.cyra.feature.auth.domain.PasswordRequirementStatus
import subha.app.cyra.ui.auth.messageKeyToStringRes
import subha.app.cyra.ui.theme.CyraError
import subha.app.cyra.ui.theme.CyraSuccess
import subha.app.cyra.ui.theme.CyraTheme

/**
 * Signup's live password checklist - one row per [PasswordRequirementStatus], updating
 * on every keystroke ([subha.app.cyra.feature.auth.presentation.SignupViewModel
 * .onPasswordChanged] recomputes the whole list). Deliberately only used under
 * `SignupScreen`'s password field: confirm-password has its own separate "do the two
 * match" check, and Login's password field has no strength requirement to show.
 */
@Composable
fun PasswordRequirementsChecklist(
    requirements: List<PasswordRequirementStatus>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(start = 4.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        requirements.forEach { requirement ->
            PasswordRequirementRow(
                satisfied = requirement.satisfied,
                text = stringResource(messageKeyToStringRes(requirement.requirement.messageKey)),
            )
        }
    }
}

@Composable
private fun PasswordRequirementRow(satisfied: Boolean, text: String) {
    val color = if (satisfied) CyraSuccess else CyraError
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (satisfied) {
            TickIcon(color = color, modifier = Modifier.size(14.dp))
        } else {
            CrossIcon(color = color, modifier = Modifier.size(14.dp))
        }
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@CyraPreviews
@Composable
private fun PasswordRequirementsChecklistPreview() {
    CyraTheme {
        PasswordRequirementsChecklist(
            requirements = AuthValidators.passwordRequirementStatuses("Abc123"),
        )
    }
}
