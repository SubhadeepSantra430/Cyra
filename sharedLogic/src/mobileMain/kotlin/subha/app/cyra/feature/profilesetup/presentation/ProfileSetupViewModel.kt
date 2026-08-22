package subha.app.cyra.feature.profilesetup.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import subha.app.cyra.core.presentation.BaseViewModel
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.feature.auth.domain.errorMessageKeyOrNull
import subha.app.cyra.feature.profilesetup.data.ProfileRepository
import subha.app.cyra.feature.profilesetup.domain.CycleRegularity
import subha.app.cyra.feature.profilesetup.domain.HeightUnit
import subha.app.cyra.feature.profilesetup.domain.MaritalStatus
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupStep
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupValidators
import subha.app.cyra.feature.profilesetup.domain.WeightUnit

/**
 * Backs the whole post-signup profile-setup flow (Android `ProfileSetupScreen`, iOS
 * `ProfileSetupView`) - one ViewModel for all 8 steps, not one per step like Auth,
 * since every step feeds the same eventual [ProfileRepository.saveProfile] write. See
 * [ProfileSetupState] for why the state shape looks the way it does.
 *
 * [userId] is threaded in at construction (from `AuthEffect.Navigate`'s
 * `NavigationEvent.NavigateToOnboarding(userId)`) rather than read from
 * `AuthRepository.currentUserId` here, so this ViewModel doesn't need an Auth
 * dependency just to know who it's saving for.
 */
class ProfileSetupViewModel(
    private val userId: String,
    private val repository: ProfileRepository,
) : BaseViewModel<ProfileSetupState, ProfileSetupEffect>(ProfileSetupState()) {

    fun onNameChanged(value: String) = setState {
        copy(name = value, nameError = if (submitAttempted) ProfileSetupValidators.validateName(value).errorMessageKeyOrNull() else null)
    }

    fun onDateOfBirthChanged(value: LocalDate) = setState {
        copy(dateOfBirth = value, dateOfBirthError = null)
    }

    fun onHeightChanged(cm: Int) = setState { copy(heightCm = cm, heightProvided = true) }

    fun onHeightUnitToggled(unit: HeightUnit) = setState { copy(heightUnit = unit) }

    fun onWeightChanged(kg: Int) = setState { copy(weightKg = kg, weightProvided = true) }

    fun onWeightUnitToggled(unit: WeightUnit) = setState { copy(weightUnit = unit) }

    fun onMaritalStatusSelected(value: MaritalStatus) = setState { copy(maritalStatus = value) }

    fun onLastPeriodStartDateChanged(value: LocalDate?) = setState { copy(lastPeriodStartDate = value) }

    fun onAverageCycleLengthChanged(value: String) = setState { copy(averageCycleLengthDays = value) }

    fun onAveragePeriodDurationChanged(value: String) = setState { copy(averagePeriodDurationDays = value) }

    fun onCycleRegularitySelected(value: CycleRegularity) = setState { copy(cycleRegularity = value) }

    fun onBackClicked() {
        val previous = currentState.step.previous() ?: return // Name has no back destination - see ProfileSetupState.showBackButton
        setState { copy(step = previous) }
    }

    /**
     * The one button at the bottom of every screen - "Next" (mandatory steps, disabled
     * until valid), "Skip" (optional steps, always enabled) or "Start My Journey"
     * ([ProfileSetupStep.AllSet]). Re-validates mandatory steps here even though the UI
     * already disables the button until valid - same defense-in-depth as
     * `SignupViewModel.onSignupClicked`.
     */
    fun onPrimaryButtonClicked() {
        when (currentState.step) {
            ProfileSetupStep.Name -> {
                val result = ProfileSetupValidators.validateName(currentState.name)
                setState { copy(submitAttempted = true, nameError = result.errorMessageKeyOrNull()) }
                if (result.errorMessageKeyOrNull() != null) return
            }
            ProfileSetupStep.Birthday -> {
                val result = ProfileSetupValidators.validateDateOfBirth(currentState.dateOfBirth)
                setState { copy(submitAttempted = true, dateOfBirthError = result.errorMessageKeyOrNull()) }
                if (result.errorMessageKeyOrNull() != null) return
            }
            else -> Unit // every other step (including AllSet) is unconditional
        }
        advanceOrSubmit()
    }

    private fun advanceOrSubmit() {
        val next = currentState.step.next()
        if (next != null) {
            setState { copy(step = next, submitAttempted = false) }
        } else {
            submitProfile()
        }
    }

    private fun submitProfile() {
        setState { copy(isSubmitting = true) }
        viewModelScope.launch {
            repository.saveProfile(userId, currentState)
                .onSuccess {
                    emitEffect(ProfileSetupEffect.ShowSuccess("profile_setup_success"))
                    emitEffect(ProfileSetupEffect.Navigate(NavigationEvent.NavigateToHome))
                }
                .onFailure { emitEffect(ProfileSetupEffect.ShowError("profile_setup_error_save_failed")) }
            setState { copy(isSubmitting = false) }
        }
    }
}
