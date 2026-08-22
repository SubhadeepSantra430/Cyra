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

    fun onLastPeriodStartDateChanged(value: LocalDate?) = setState {
        // Picking a real date after having said "I don't remember" supersedes it.
        copy(lastPeriodStartDate = value, lastPeriodUnknown = false)
    }

    /** Confirming the "I don't remember" dialog satisfies this mandatory step without an exact date, and moves on immediately - same as tapping "Next" would once valid. */
    fun onLastPeriodUnknownConfirmed() {
        setState { copy(lastPeriodUnknown = true) }
        advanceOrSubmit()
    }

    fun onAverageCycleLengthChanged(value: String) = setState { copy(averageCycleLengthDays = value) }

    fun onAveragePeriodDurationChanged(value: String) = setState { copy(averagePeriodDurationDays = value) }

    fun onCycleRegularitySelected(value: CycleRegularity) = setState { copy(cycleRegularity = value) }

    fun onBackClicked() {
        val previous = currentState.step.previous() ?: return // Name has no back destination - see ProfileSetupState.showBackButton
        setState { copy(step = previous) }
    }

    /**
     * The single full-width button on mandatory steps (Name, Birthday, Last Period) and
     * the completion screen ("Start My Journey") - see [ProfileSetupState
     * .showDualButtons] for the optional steps' separate Skip/Next pair. Re-validates
     * mandatory steps here even though the UI already disables the button until valid -
     * same defense-in-depth as `SignupViewModel.onSignupClicked`.
     */
    fun onNextClicked() {
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
            ProfileSetupStep.LastPeriod -> {
                if (!currentState.isPrimaryButtonEnabled) return // UI already disables the button until valid
            }
            else -> Unit // the optional steps' own "Next" (see onSkipClicked) and AllSet are unconditional
        }
        advanceOrSubmit()
    }

    /**
     * The "Skip" half of an optional step's button pair - explicitly discards whatever
     * that step's field(s) currently hold (even a slider the user already dragged),
     * unlike that same step's "Next", which keeps it. Only meaningful on steps where
     * [ProfileSetupState.showDualButtons] is true.
     */
    fun onSkipClicked() {
        when (currentState.step) {
            ProfileSetupStep.Height -> setState { copy(heightProvided = false) }
            ProfileSetupStep.Weight -> setState { copy(weightProvided = false) }
            ProfileSetupStep.MaritalStatus -> setState { copy(maritalStatus = null) }
            ProfileSetupStep.CycleInfo -> setState {
                copy(averageCycleLengthDays = "", averagePeriodDurationDays = "", cycleRegularity = CycleRegularity.NotSure)
            }
            else -> Unit
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
