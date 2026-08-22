package subha.app.cyra.feature.auth.domain

/**
 * Result of validating a single form field. [Invalid.messageKey] is a string-resource
 * *key* (e.g. `"auth_error_email_invalid"`), not literal English - keeps this file free
 * of platform string lookup, exactly like onboarding's `titleKey`/`descriptionKey`
 * pattern. Each platform resolves the key to localized copy at render time.
 */
sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val messageKey: String) : ValidationResult
}

/** Convenience: the message key to surface, or `null` when the field is valid. */
fun ValidationResult.errorMessageKeyOrNull(): String? =
    (this as? ValidationResult.Invalid)?.messageKey
