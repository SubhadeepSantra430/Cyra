package subha.app.cyra.feature.profilesetup.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import subha.app.cyra.feature.auth.domain.ValidationResult
import subha.app.cyra.feature.auth.domain.errorMessageKeyOrNull

class ProfileSetupValidatorsTest {

    @Test
    fun blankName_isRequired() {
        assertEquals("profile_setup_error_name_required", ProfileSetupValidators.validateName("").errorMessageKeyOrNull())
    }

    @Test
    fun nonBlankName_isValid() {
        assertIs<ValidationResult.Valid>(ProfileSetupValidators.validateName("Ada"))
    }

    @Test
    fun missingDateOfBirth_isRequired() {
        assertEquals("profile_setup_error_dob_required", ProfileSetupValidators.validateDateOfBirth(null).errorMessageKeyOrNull())
    }

    @Test
    fun presentDateOfBirth_isValid() {
        assertIs<ValidationResult.Valid>(ProfileSetupValidators.validateDateOfBirth(LocalDate(2000, 7, 23)))
    }
}
