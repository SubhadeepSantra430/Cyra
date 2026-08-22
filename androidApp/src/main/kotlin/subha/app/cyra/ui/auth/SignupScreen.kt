package subha.app.cyra.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import subha.app.cyra.feature.auth.presentation.AuthEffect
import subha.app.cyra.feature.auth.presentation.SignupState
import subha.app.cyra.feature.auth.presentation.SignupViewModel
import subha.app.cyra.ui.components.CyraBackButton
import subha.app.cyra.ui.components.CyraDividerWithLabel
import subha.app.cyra.ui.components.CyraLinkText
import subha.app.cyra.ui.components.CyraPreviews
import subha.app.cyra.ui.components.CyraPrimaryButton
import subha.app.cyra.ui.components.CyraSocialButton
import subha.app.cyra.ui.components.CyraTermsCheckboxRow
import subha.app.cyra.ui.components.CyraTextField
import subha.app.cyra.ui.components.EnvelopeIcon
import subha.app.cyra.ui.components.GoogleIcon
import subha.app.cyra.ui.components.LockIcon
import subha.app.cyra.ui.theme.CyraTheme

/**
 * Backed by [SignupViewModel]. No name fields, by design - dropped from the reference
 * screenshot for this pass, to be added back in a later module. See [LoginScreen] for
 * the shared shape (state collection, side-effect handling, Google-only social button).
 */
@Composable
fun SignupScreen(
    onNavigate: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var errorMessageKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is AuthEffect.Navigate -> onNavigate(effect.event)
                is AuthEffect.ShowError -> errorMessageKey = effect.messageKey
            }
        }
    }

    fun onGoogleClick() {
        val webClientId = context.getString(R.string.google_web_client_id)
        if (webClientId == GOOGLE_WEB_CLIENT_ID_PLACEHOLDER) {
            errorMessageKey = "auth_error_google_not_configured"
            return
        }
        coroutineScope.launch {
            runCatching { signInWithGoogleCredentialManager(context, webClientId) }
                .onSuccess { idToken -> viewModel.onGoogleSignInResult(idToken, accessToken = null) }
                .onFailure { viewModel.onGoogleSignInFailed() }
        }
    }

    SignupScreenContent(
        state = state,
        errorMessage = errorMessageKey?.let { stringResource(messageKeyToStringRes(it)) },
        onBackClick = viewModel::onBackClicked,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChanged,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onTermsAgreedChange = viewModel::onTermsAgreedChanged,
        onSignupClick = viewModel::onSignupClicked,
        onGoogleClick = ::onGoogleClick,
        onLoginLinkClick = viewModel::onLoginLinkClicked,
        modifier = modifier,
    )
}

@Composable
private fun SignupScreenContent(
    state: SignupState,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onTermsAgreedChange: (Boolean) -> Unit,
    onSignupClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onLoginLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Terms/Privacy links have no destination in this pass - deliberately no-op, since
    // no legal-content screen exists yet anywhere in the app.
    val onTermsClick = {}
    val onPrivacyClick = {}

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        CyraBackButton(onClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.auth_signup_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.auth_signup_subtitle),
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

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

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
        Spacer(modifier = Modifier.height(16.dp))
        CyraTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = stringResource(R.string.auth_confirm_password_placeholder),
            leadingIcon = { LockIcon() },
            isPassword = true,
            isPasswordVisible = state.isConfirmPasswordVisible,
            onTogglePasswordVisibility = onToggleConfirmPasswordVisibility,
            errorText = state.confirmPasswordError?.let { stringResource(messageKeyToStringRes(it)) },
            enabled = !state.isBusy,
        )

        Spacer(modifier = Modifier.height(16.dp))
        CyraTermsCheckboxRow(
            checked = state.agreedToTerms,
            onCheckedChange = onTermsAgreedChange,
            onTermsClick = onTermsClick,
            onPrivacyClick = onPrivacyClick,
        )
        val termsError = state.termsError
        if (termsError != null) {
            Text(
                text = stringResource(messageKeyToStringRes(termsError)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 32.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        CyraPrimaryButton(
            text = stringResource(R.string.auth_sign_up_button),
            onClick = onSignupClick,
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
            prefix = stringResource(R.string.auth_has_account_prefix),
            linkText = stringResource(R.string.auth_login_link),
            onLinkClick = onLoginLinkClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@CyraPreviews
@Composable
private fun SignupScreenPreview() {
    CyraTheme {
        SignupScreenContent(
            state = SignupState(),
            errorMessage = null,
            onBackClick = {},
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onConfirmPasswordChange = {},
            onToggleConfirmPasswordVisibility = {},
            onTermsAgreedChange = {},
            onSignupClick = {},
            onGoogleClick = {},
            onLoginLinkClick = {},
        )
    }
}
