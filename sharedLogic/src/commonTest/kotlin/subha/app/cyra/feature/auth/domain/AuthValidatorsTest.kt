package subha.app.cyra.feature.auth.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AuthValidatorsTest {

    @Test
    fun validEmail_isValid() {
        assertIs<ValidationResult.Valid>(AuthValidators.validateEmail("user@example.com"))
    }

    @Test
    fun blankEmail_isRequired() {
        val result = AuthValidators.validateEmail("")
        assertEquals("auth_error_email_required", result.errorMessageKeyOrNull())
    }

    @Test
    fun malformedEmail_isInvalid() {
        val result = AuthValidators.validateEmail("not-an-email")
        assertEquals("auth_error_email_invalid", result.errorMessageKeyOrNull())
    }

    @Test
    fun validPassword_isValid() {
        assertIs<ValidationResult.Valid>(AuthValidators.validatePassword("Str0ng!Pass"))
    }

    @Test
    fun tooShortPassword_isWeak() {
        assertEquals("auth_error_password_weak", AuthValidators.validatePassword("Sh0rt!").errorMessageKeyOrNull())
    }

    @Test
    fun passwordMissingUppercase_isWeak() {
        assertEquals("auth_error_password_weak", AuthValidators.validatePassword("str0ng!pass").errorMessageKeyOrNull())
    }

    @Test
    fun passwordMissingLowercase_isWeak() {
        assertEquals("auth_error_password_weak", AuthValidators.validatePassword("STR0NG!PASS").errorMessageKeyOrNull())
    }

    @Test
    fun passwordMissingDigit_isWeak() {
        assertEquals("auth_error_password_weak", AuthValidators.validatePassword("Strong!Pass").errorMessageKeyOrNull())
    }

    @Test
    fun passwordMissingSpecialChar_isWeak() {
        assertEquals("auth_error_password_weak", AuthValidators.validatePassword("Str0ngPass").errorMessageKeyOrNull())
    }

    @Test
    fun matchingPasswords_areValid() {
        assertIs<ValidationResult.Valid>(AuthValidators.validatePasswordsMatch("Str0ng!Pass", "Str0ng!Pass"))
    }

    @Test
    fun mismatchedPasswords_areInvalid() {
        val result = AuthValidators.validatePasswordsMatch("Str0ng!Pass", "Different1!")
        assertEquals("auth_error_passwords_mismatch", result.errorMessageKeyOrNull())
    }

    @Test
    fun termsAgreed_isValid() {
        assertIs<ValidationResult.Valid>(AuthValidators.validateTermsAgreed(true))
    }

    @Test
    fun termsNotAgreed_isInvalid() {
        assertEquals("auth_error_terms_required", AuthValidators.validateTermsAgreed(false).errorMessageKeyOrNull())
    }

    @Test
    fun passwordRequirementStatuses_allSatisfied_whenPasswordIsStrong() {
        val statuses = AuthValidators.passwordRequirementStatuses("Str0ng!Pass")
        assertEquals(true, statuses.all { it.satisfied })
    }

    @Test
    fun passwordRequirementStatuses_flagsOnlyWhatsMissing() {
        // Lowercase + digit only - missing uppercase and special char, long enough.
        val statuses = AuthValidators.passwordRequirementStatuses("longpassword1")
        val byRequirement = statuses.associate { it.requirement to it.satisfied }
        assertEquals(true, byRequirement[PasswordRequirement.MIN_LENGTH])
        assertEquals(true, byRequirement[PasswordRequirement.LOWERCASE])
        assertEquals(true, byRequirement[PasswordRequirement.DIGIT])
        assertEquals(false, byRequirement[PasswordRequirement.UPPERCASE])
        assertEquals(false, byRequirement[PasswordRequirement.SPECIAL_CHAR])
    }

    @Test
    fun passwordRequirementStatuses_noneSatisfied_whenPasswordIsEmpty() {
        val statuses = AuthValidators.passwordRequirementStatuses("")
        assertEquals(true, statuses.none { it.satisfied })
    }
}
