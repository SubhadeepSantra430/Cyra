package subha.app.cyra.feature.auth.presentation

/**
 * [submitAttempted] gates when field errors are shown - typing shouldn't red-underline a
 * field before the user has ever tried to submit, but every keystroke after a failed
 * attempt re-validates live so the error can clear as soon as it's fixed.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val isGoogleSigningIn: Boolean = false,
    val isAppleSigningIn: Boolean = false,
    val submitAttempted: Boolean = false,
) {
    val isBusy: Boolean get() = isSubmitting || isGoogleSigningIn || isAppleSigningIn
}
