package subha.app.cyra.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import subha.app.cyra.R
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.feature.auth.presentation.ForgotPasswordState
import subha.app.cyra.feature.auth.presentation.ForgotPasswordViewModel
import subha.app.cyra.ui.components.CyraBackButton
import subha.app.cyra.ui.components.CyraPreviews
import subha.app.cyra.ui.components.CyraPrimaryButton
import subha.app.cyra.ui.components.CyraTextButton
import subha.app.cyra.ui.components.CyraTextField
import subha.app.cyra.ui.components.EnvelopeIcon
import subha.app.cyra.ui.theme.CyraTheme

/**
 * The minimal screen reached from Login's "Forgot password?" link - not in the reference
 * design, kept deliberately small (headline, one field, submit) since Firebase gives us
 * the whole reset-email flow for free via [ForgotPasswordViewModel].
 */
@Composable
fun ForgotPasswordScreen(
    onNavigate: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Field-level validation still renders via state.emailError; anything else (e.g. a
    // network failure sending the reset email) now surfaces through the global snackbar.
    HandleAuthEffects(sideEffect = viewModel.sideEffect, onNavigate = onNavigate)

    ForgotPasswordScreenContent(
        state = state,
        onBackClick = viewModel::onBackClicked,
        onEmailChange = viewModel::onEmailChanged,
        onSubmitClick = viewModel::onSubmitClicked,
        modifier = modifier,
    )
}

@Composable
private fun ForgotPasswordScreenContent(
    state: ForgotPasswordState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // Screens draw edge-to-edge (see MainActivity's enableEdgeToEdge()) - without
            // this, CyraBackButton (the first child) sits under the status bar.
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        CyraBackButton(onClick = onBackClick)

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.auth_forgot_password_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_forgot_password_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.emailSent) {
            Text(
                text = stringResource(R.string.auth_forgot_password_sent_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.auth_forgot_password_sent_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
            CyraTextButton(text = stringResource(R.string.auth_back_to_login_button), onClick = onBackClick)
        } else {
            CyraTextField(
                value = state.email,
                onValueChange = onEmailChange,
                placeholder = stringResource(R.string.auth_email_placeholder),
                leadingIcon = { EnvelopeIcon() },
                errorText = state.emailError?.let { stringResource(messageKeyToStringRes(it)) },
                enabled = !state.isSubmitting,
            )
            Spacer(modifier = Modifier.height(24.dp))
            CyraPrimaryButton(
                text = stringResource(R.string.auth_send_reset_link_button),
                onClick = onSubmitClick,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@CyraPreviews
@Composable
private fun ForgotPasswordScreenPreview() {
    CyraTheme {
        ForgotPasswordScreenContent(
            state = ForgotPasswordState(),
            onBackClick = {},
            onEmailChange = {},
            onSubmitClick = {},
        )
    }
}
