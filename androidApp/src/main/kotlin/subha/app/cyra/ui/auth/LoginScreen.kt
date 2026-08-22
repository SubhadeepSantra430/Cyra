package subha.app.cyra.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import subha.app.cyra.R
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.feature.auth.presentation.LoginState
import subha.app.cyra.feature.auth.presentation.LoginViewModel
import subha.app.cyra.ui.components.CyraBackButton
import subha.app.cyra.ui.components.CyraDividerWithLabel
import subha.app.cyra.ui.components.CyraLinkText
import subha.app.cyra.ui.components.CyraPreviews
import subha.app.cyra.ui.components.CyraPrimaryButton
import subha.app.cyra.ui.components.CyraSocialButton
import subha.app.cyra.ui.components.CyraTextField
import subha.app.cyra.ui.components.EnvelopeIcon
import subha.app.cyra.ui.components.GoogleIcon
import subha.app.cyra.ui.components.LocalCyraSnackbarController
import subha.app.cyra.ui.components.LockIcon
import subha.app.cyra.ui.theme.CyraTheme

/**
 * Backed by [LoginViewModel] - all validation/Firebase calls happen there, this
 * composable only renders state and forwards events. Google-only social sign-in on
 * Android (Apple is iOS-only, per product decision); see `LoginView.swift` for the
 * mirrored iOS screen, which shows both.
 */
@Composable
fun LoginScreen(
    onNavigate: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarController = LocalCyraSnackbarController.current

    HandleAuthEffects(sideEffect = viewModel.sideEffect, onNavigate = onNavigate)

    fun onGoogleClick() {
        val webClientId = context.getString(R.string.google_web_client_id)
        if (webClientId == GOOGLE_WEB_CLIENT_ID_PLACEHOLDER) {
            // Google Sign-In isn't enabled in Firebase console yet - fail locally
            // instead of ever touching Credential Manager or the ViewModel's shared
            // side-effect channel (which would race with this directly-shown message).
            snackbarController.showError(context.getString(R.string.auth_error_google_not_configured))
            return
        }
        coroutineScope.launch {
            runCatching { signInWithGoogleCredentialManager(context, webClientId) }
                .onSuccess { idToken -> viewModel.onGoogleSignInResult(idToken, accessToken = null) }
                .onFailure { viewModel.onGoogleSignInFailed() }
        }
    }

    LoginScreenContent(
        state = state,
        onBackClick = viewModel::onBackClicked,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onForgotPasswordClick = viewModel::onForgotPasswordClicked,
        onLoginClick = viewModel::onLoginClicked,
        onGoogleClick = ::onGoogleClick,
        onSignupLinkClick = viewModel::onSignupLinkClicked,
        modifier = modifier,
    )
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onSignupLinkClick: () -> Unit,
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

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.auth_login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.auth_login_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Image(
                painter = painterResource(R.drawable.auth_illustration),
                contentDescription = null,
                modifier = Modifier.width(140.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        CyraTextField(
            value = state.email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.auth_email_placeholder),
            leadingIcon = { EnvelopeIcon() },
            errorText = state.emailError?.let { stringResource(messageKeyToStringRes(it)) },
            enabled = !state.isBusy,
        )
        Spacer(modifier = Modifier.height(16.dp))
        CyraTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.auth_password_placeholder),
            leadingIcon = { LockIcon() },
            isPassword = true,
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            errorText = state.passwordError?.let { stringResource(messageKeyToStringRes(it)) },
            enabled = !state.isBusy,
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            CyraLinkText(prefix = "", linkText = stringResource(R.string.auth_forgot_password_link), onLinkClick = onForgotPasswordClick)
        }

        Spacer(modifier = Modifier.height(16.dp))
        CyraPrimaryButton(
            text = stringResource(R.string.auth_log_in_button),
            onClick = onLoginClick,
            enabled = !state.isBusy,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))
        CyraDividerWithLabel(text = stringResource(R.string.auth_or_continue_with))

        Spacer(modifier = Modifier.height(16.dp))
        CyraSocialButton(
            text = stringResource(R.string.auth_continue_with_google),
            icon = { GoogleIcon() },
            onClick = onGoogleClick,
            enabled = !state.isBusy,
        )

        Spacer(modifier = Modifier.height(24.dp))
        CyraLinkText(
            prefix = stringResource(R.string.auth_no_account_prefix),
            linkText = stringResource(R.string.auth_signup_link),
            onLinkClick = onSignupLinkClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@CyraPreviews
@Composable
private fun LoginScreenPreview() {
    CyraTheme {
        LoginScreenContent(
            state = LoginState(),
            onBackClick = {},
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onForgotPasswordClick = {},
            onLoginClick = {},
            onGoogleClick = {},
            onSignupLinkClick = {},
        )
    }
}
