package subha.app.cyra.feature.profilesetup.domain

import kotlinx.datetime.LocalDate
import subha.app.cyra.feature.auth.domain.ValidationResult

/**
 * The only two mandatory fields in the flow ([ProfileSetupStep.Name] and
 * [ProfileSetupStep.Birthday]) - every other step is optional and has nothing to
 * validate. Reuses Auth's [ValidationResult] rather than a second copy of the same
 * two-case sealed type - if a third feature ever needs it, that's the sign to hoist it
 * to a shared `core.domain` package, not before.
 */
object ProfileSetupValidators {

    fun validateName(value: String): ValidationResult = when {
        value.isBlank() -> ValidationResult.Invalid("profile_setup_error_name_required")
        else -> ValidationResult.Valid
    }

    fun validateDateOfBirth(value: LocalDate?): ValidationResult = when (value) {
        null -> ValidationResult.Invalid("profile_setup_error_dob_required")
        else -> ValidationResult.Valid
    }
}
