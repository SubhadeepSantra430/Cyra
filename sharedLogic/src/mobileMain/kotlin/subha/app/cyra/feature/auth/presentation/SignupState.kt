package subha.app.cyra.feature.auth.presentation

import subha.app.cyra.feature.auth.domain.AuthValidators
import subha.app.cyra.feature.auth.domain.PasswordRequirementStatus

/**
 * No name fields by design - deliberately dropped from the reference screenshot for
 * this pass, to be added back in a later module.
 */
data class SignupState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val agreedToTerms: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    // No termsError field - unlike the other fields, "agree to terms" has no adjacent
    // input box to carry an inline error, so SignupViewModel.onSignupClicked surfaces
    // that failure through the global snackbar (AuthEffect.ShowError) instead.
    // Live per-rule password checklist - recomputed on every keystroke (see
    // SignupViewModel.onPasswordChanged), independent of submitAttempted/passwordError
    // since it's meant to guide the user as they type, not only after a failed submit.
    // Signup's password field only; confirm-password and Login's password field don't
    // show this checklist.
    val passwordRequirements: List<PasswordRequirementStatus> = AuthValidators.passwordRequirementStatuses(""),
    val isSubmitting: Boolean = false,
    val isGoogleSigningIn: Boolean = false,
    val isAppleSigningIn: Boolean = false,
    val submitAttempted: Boolean = false,
) {
    val isBusy: Boolean get() = isSubmitting || isGoogleSigningIn || isAppleSigningIn
}
