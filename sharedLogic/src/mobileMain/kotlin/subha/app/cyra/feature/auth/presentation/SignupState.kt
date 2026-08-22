package subha.app.cyra.feature.auth.presentation

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
    val termsError: String? = null,
    val isSubmitting: Boolean = false,
    val isGoogleSigningIn: Boolean = false,
    val isAppleSigningIn: Boolean = false,
    val submitAttempted: Boolean = false,
) {
    val isBusy: Boolean get() = isSubmitting || isGoogleSigningIn || isAppleSigningIn
}
