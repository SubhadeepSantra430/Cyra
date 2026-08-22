package subha.app.cyra.feature.auth.domain

/**
 * Pure, regex-based field validation - the single source of truth for what a valid
 * email/password looks like on both platforms. Deliberately has zero platform
 * dependencies so it lives in `commonMain` (works even on js/wasmJs, though only
 * mobile ships an Auth UI today) and is unit-testable without Android/iOS test infra.
 *
 * Called ONLY from ViewModels ([subha.app.cyra.feature.auth.presentation.LoginViewModel]
 * etc.) - never inline inside a button's onClick/action body on either platform. This is
 * what keeps validation logic decoupled from the UI layer.
 */
object AuthValidators {

    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    // Min 8 chars, at least one lowercase, one uppercase, one digit, one special char.
    private val PASSWORD_REGEX =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")

    fun validateEmail(value: String): ValidationResult = when {
        value.isBlank() -> ValidationResult.Invalid("auth_error_email_required")
        EMAIL_REGEX.matches(value) -> ValidationResult.Valid
        else -> ValidationResult.Invalid("auth_error_email_invalid")
    }

    fun validatePassword(value: String): ValidationResult = when {
        value.isBlank() -> ValidationResult.Invalid("auth_error_password_required")
        PASSWORD_REGEX.matches(value) -> ValidationResult.Valid
        else -> ValidationResult.Invalid("auth_error_password_weak")
    }

    fun validatePasswordsMatch(password: String, confirmPassword: String): ValidationResult = when {
        confirmPassword.isBlank() -> ValidationResult.Invalid("auth_error_confirm_password_required")
        password != confirmPassword -> ValidationResult.Invalid("auth_error_passwords_mismatch")
        else -> ValidationResult.Valid
    }

    fun validateTermsAgreed(agreed: Boolean): ValidationResult =
        if (agreed) ValidationResult.Valid else ValidationResult.Invalid("auth_error_terms_required")
}
