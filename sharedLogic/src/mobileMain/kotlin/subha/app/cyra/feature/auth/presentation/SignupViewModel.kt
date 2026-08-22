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

/** Backs both the Android `SignupScreen` and iOS `SignupView`. See [LoginViewModel] for the shared shape. */
class SignupViewModel(private val repository: AuthRepository) : BaseViewModel<SignupState, AuthEffect>(SignupState()) {

    fun onEmailChanged(value: String) = setState {
        copy(email = value, emailError = if (submitAttempted) AuthValidators.validateEmail(value).errorMessageKeyOrNull() else null)
    }

    fun onPasswordChanged(value: String) = setState {
        copy(
            password = value,
            passwordError = if (submitAttempted) AuthValidators.validatePassword(value).errorMessageKeyOrNull() else null,
            confirmPasswordError = if (submitAttempted) AuthValidators.validatePasswordsMatch(value, confirmPassword).errorMessageKeyOrNull() else null,
        )
    }

    fun onConfirmPasswordChanged(value: String) = setState {
        copy(
            confirmPassword = value,
            confirmPasswordError = if (submitAttempted) AuthValidators.validatePasswordsMatch(password, value).errorMessageKeyOrNull() else null,
        )
    }

    fun onTogglePasswordVisibility() = setState { copy(isPasswordVisible = !isPasswordVisible) }

    fun onToggleConfirmPasswordVisibility() = setState { copy(isConfirmPasswordVisible = !isConfirmPasswordVisible) }

    fun onTermsAgreedChanged(agreed: Boolean) = setState {
        copy(agreedToTerms = agreed, termsError = if (submitAttempted) AuthValidators.validateTermsAgreed(agreed).errorMessageKeyOrNull() else null)
    }

    fun onLoginLinkClicked() = emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateToLogin))

    fun onBackClicked() = emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateBack))

    fun onSignupClicked() {
        val emailResult = AuthValidators.validateEmail(currentState.email)
        val passwordResult = AuthValidators.validatePassword(currentState.password)
        val confirmResult = AuthValidators.validatePasswordsMatch(currentState.password, currentState.confirmPassword)
        val termsResult = AuthValidators.validateTermsAgreed(currentState.agreedToTerms)
        setState {
            copy(
                submitAttempted = true,
                emailError = emailResult.errorMessageKeyOrNull(),
                passwordError = passwordResult.errorMessageKeyOrNull(),
                confirmPasswordError = confirmResult.errorMessageKeyOrNull(),
                termsError = termsResult.errorMessageKeyOrNull(),
            )
        }
        val allValid = listOf(emailResult, passwordResult, confirmResult, termsResult).all { it is ValidationResult.Valid }
        if (!allValid) return

        setState { copy(isSubmitting = true) }
        viewModelScope.launch {
            repository.signup(currentState.email, currentState.password)
                .onSuccess { emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateToHome)) }
                .onFailure { emitEffect(AuthEffect.ShowError(AuthErrorMapper.toMessageKey(it))) }
            setState { copy(isSubmitting = false) }
        }
    }

    fun onGoogleSignInResult(idToken: String, accessToken: String?) {
        setState { copy(isGoogleSigningIn = true) }
        viewModelScope.launch {
            repository.signInWithGoogle(idToken, accessToken)
                .onSuccess { emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateToHome)) }
                .onFailure { emitEffect(AuthEffect.ShowError(AuthErrorMapper.toMessageKey(it))) }
            setState { copy(isGoogleSigningIn = false) }
        }
    }

    fun onGoogleSignInFailed() = emitEffect(AuthEffect.ShowError("auth_error_google_failed"))

    fun onAppleSignInResult(idToken: String, rawNonce: String) {
        setState { copy(isAppleSigningIn = true) }
        viewModelScope.launch {
            repository.signInWithApple(idToken, rawNonce)
                .onSuccess { emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateToHome)) }
                .onFailure { emitEffect(AuthEffect.ShowError(AuthErrorMapper.toMessageKey(it))) }
            setState { copy(isAppleSigningIn = false) }
        }
    }

    fun onAppleSignInFailed() = emitEffect(AuthEffect.ShowError("auth_error_apple_failed"))
}
