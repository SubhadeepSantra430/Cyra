package subha.app.cyra.ui.auth

import androidx.annotation.StringRes
import subha.app.cyra.R

/**
 * Maps a `messageKey` from [subha.app.cyra.feature.auth.domain.ValidationResult.Invalid]
 * or `AuthEffect.ShowError` to its Android string resource - keeps the shared domain
 * layer free of any `R.string` reference (it only knows key names).
 */
@StringRes
fun messageKeyToStringRes(key: String): Int = when (key) {
    "auth_error_email_required" -> R.string.auth_error_email_required
    "auth_error_email_invalid" -> R.string.auth_error_email_invalid
    "auth_error_password_required" -> R.string.auth_error_password_required
    "auth_error_password_weak" -> R.string.auth_error_password_weak
    "auth_error_confirm_password_required" -> R.string.auth_error_confirm_password_required
    "auth_error_passwords_mismatch" -> R.string.auth_error_passwords_mismatch
    "auth_error_terms_required" -> R.string.auth_error_terms_required
    "auth_error_email_in_use" -> R.string.auth_error_email_in_use
    "auth_error_invalid_credentials" -> R.string.auth_error_invalid_credentials
    "auth_error_recent_login_required" -> R.string.auth_error_recent_login_required
    "auth_error_network" -> R.string.auth_error_network
    "auth_error_google_failed" -> R.string.auth_error_google_failed
    "auth_error_google_not_configured" -> R.string.auth_error_google_not_configured
    "auth_error_apple_failed" -> R.string.auth_error_apple_failed
    else -> R.string.auth_error_generic
}
