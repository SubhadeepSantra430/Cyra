package subha.app.cyra.feature.auth.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import subha.app.cyra.core.presentation.BaseViewModel
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.feature.auth.data.AuthRepository
import subha.app.cyra.feature.auth.domain.AuthErrorMapper
import subha.app.cyra.feature.auth.domain.AuthValidators
import subha.app.cyra.feature.auth.domain.ValidationResult
import subha.app.cyra.feature.auth.domain.errorMessageKeyOrNull

/**
 * Backs both the Android `LoginScreen` and iOS `LoginView` - all validation and Firebase
 * calls happen here, never in either platform's UI layer. See [AuthValidators] for the
 * regex rules and [subha.app.cyra.feature.auth.di.provideLoginViewModel] for how Swift
 * resolves this instance.
 */
class LoginViewModel(private val repository: AuthRepository) : BaseViewModel<LoginState, AuthEffect>(LoginState()) {

    fun onEmailChanged(value: String) = setState {
        copy(email = value, emailError = if (submitAttempted) AuthValidators.validateEmail(value).errorMessageKeyOrNull() else null)
    }

    fun onPasswordChanged(value: String) = setState {
        copy(password = value, passwordError = if (submitAttempted) AuthValidators.validatePassword(value).errorMessageKeyOrNull() else null)
    }

    fun onTogglePasswordVisibility() = setState { copy(isPasswordVisible = !isPasswordVisible) }

    fun onForgotPasswordClicked() = emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateToForgotPassword))

    fun onSignupLinkClicked() = emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateToSignup))

    fun onBackClicked() = emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateBack))

    fun onLoginClicked() {
        val emailResult = AuthValidators.validateEmail(currentState.email)
        val passwordResult = AuthValidators.validatePassword(currentState.password)
        setState {
            copy(
                submitAttempted = true,
                emailError = emailResult.errorMessageKeyOrNull(),
                passwordError = passwordResult.errorMessageKeyOrNull(),
            )
        }
        if (emailResult !is ValidationResult.Valid || passwordResult !is ValidationResult.Valid) return

        setState { copy(isSubmitting = true) }
        viewModelScope.launch {
            repository.login(currentState.email, currentState.password)
                .onSuccess {
                    emitEffect(AuthEffect.ShowSuccess("auth_success_login"))
                    emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateToHome))
                }
                .onFailure { emitEffect(AuthEffect.ShowError(AuthErrorMapper.toMessageKey(it))) }
            setState { copy(isSubmitting = false) }
        }
    }

    /** [accessToken] is null on Android - Credential Manager only yields an ID token. */
    fun onGoogleSignInResult(idToken: String, accessToken: String?) {
        setState { copy(isGoogleSigningIn = true) }
        viewModelScope.launch {
            repository.signInWithGoogle(idToken, accessToken)
                .onSuccess { isNewUser ->
                    emitEffect(AuthEffect.ShowSuccess("auth_success_login"))
                    navigateAfterAuth(isNewUser)
                }
                .onFailure { emitEffect(AuthEffect.ShowError(AuthErrorMapper.toMessageKey(it))) }
            setState { copy(isGoogleSigningIn = false) }
        }
    }

    fun onGoogleSignInFailed() = emitEffect(AuthEffect.ShowError("auth_error_google_failed"))

    fun onAppleSignInResult(idToken: String, rawNonce: String) {
        setState { copy(isAppleSigningIn = true) }
        viewModelScope.launch {
            repository.signInWithApple(idToken, rawNonce)
                .onSuccess { isNewUser ->
                    emitEffect(AuthEffect.ShowSuccess("auth_success_login"))
                    navigateAfterAuth(isNewUser)
                }
                .onFailure { emitEffect(AuthEffect.ShowError(AuthErrorMapper.toMessageKey(it))) }
            setState { copy(isAppleSigningIn = false) }
        }
    }

    fun onAppleSignInFailed() = emitEffect(AuthEffect.ShowError("auth_error_apple_failed"))

    /**
     * Google/Apple sign-in can be tapped from the Login screen but still turn out to be
     * someone's very first sign-in ever (Firebase auto-creates the account) - route
     * those into profile-setup exactly like a fresh Signup would, same as
     * [SignupViewModel.navigateAfterAuth]. Plain email/password login never reaches
     * this - by definition it's always an existing account, so [onLoginClicked] always
     * emits `NavigateToHome` directly.
     */
    private fun navigateAfterAuth(isNewUser: Boolean) {
        val userId = repository.currentUserId
        val event = if (isNewUser && userId != null) {
            NavigationEvent.NavigateToOnboarding(userId)
        } else {
            NavigationEvent.NavigateToHome
        }
        emitEffect(AuthEffect.Navigate(event))
    }
}
