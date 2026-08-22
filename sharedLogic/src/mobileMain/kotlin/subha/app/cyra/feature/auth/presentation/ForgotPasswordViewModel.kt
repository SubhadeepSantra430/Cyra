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

/** Backs the minimal "reset your password" screen reached from Login's "Forgot password?" link. */
class ForgotPasswordViewModel(private val repository: AuthRepository) :
    BaseViewModel<ForgotPasswordState, AuthEffect>(ForgotPasswordState()) {

    fun onEmailChanged(value: String) = setState {
        copy(email = value, emailError = if (submitAttempted) AuthValidators.validateEmail(value).errorMessageKeyOrNull() else null)
    }

    fun onBackClicked() = emitEffect(AuthEffect.Navigate(NavigationEvent.NavigateBack))

    fun onSubmitClicked() {
        val emailResult = AuthValidators.validateEmail(currentState.email)
        setState { copy(submitAttempted = true, emailError = emailResult.errorMessageKeyOrNull()) }
        if (emailResult !is ValidationResult.Valid) return

        setState { copy(isSubmitting = true) }
        viewModelScope.launch {
            repository.sendPasswordReset(currentState.email)
                .onSuccess { setState { copy(emailSent = true) } }
                .onFailure { emitEffect(AuthEffect.ShowError(AuthErrorMapper.toMessageKey(it))) }
            setState { copy(isSubmitting = false) }
        }
    }
}
