package subha.app.cyra.feature.auth.presentation

data class ForgotPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val isSubmitting: Boolean = false,
    val submitAttempted: Boolean = false,
    val emailSent: Boolean = false,
)
