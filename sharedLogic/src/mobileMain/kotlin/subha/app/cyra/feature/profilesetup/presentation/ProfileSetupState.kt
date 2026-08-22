package subha.app.cyra.feature.profilesetup.presentation

import kotlinx.datetime.LocalDate
import subha.app.cyra.feature.profilesetup.domain.CycleRegularity
import subha.app.cyra.feature.profilesetup.domain.HeightUnit
import subha.app.cyra.feature.profilesetup.domain.MaritalStatus
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupCategory
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupStep
import subha.app.cyra.feature.profilesetup.domain.WeightUnit

// Deliberately 0, not a plausible-looking height/weight - a slider needs *some* concrete
// value to render, but pre-filling it with, say, 160cm/60kg would look like the user
// already answered. 0 is an unambiguous "untouched" sentinel; the paired text field
// shows it as literally "0" until the user drags the slider or types over it.
private const val DEFAULT_HEIGHT_CM = 0
private const val DEFAULT_WEIGHT_KG = 0

/**
 * One accumulating state object for the whole flow - unlike Auth's three separate
 * screens/ViewModels, every step here feeds the same eventual profile write, so a
 * single state (with a [step] pointer) is the simpler shape than one ViewModel per step.
 *
 * [heightCm]/[weightKg] always hold a concrete slider position (Compose's `Slider` and
 * SwiftUI's `Slider` both require a non-null value to render) - [heightProvided]/
 * [weightProvided] track whether the user actually touched either control (dragged the
 * slider or typed the paired text field), so [ProfileSetupViewModel] knows not to
 * persist an untouched default as if the user had chosen it.
 */
data class ProfileSetupState(
    val step: ProfileSetupStep = ProfileSetupStep.Name,
    val name: String = "",
    val nameError: String? = null,
    val dateOfBirth: LocalDate? = null,
    val dateOfBirthError: String? = null,
    val heightCm: Int = DEFAULT_HEIGHT_CM,
    val heightProvided: Boolean = false,
    val heightUnit: HeightUnit = HeightUnit.CM,
    val weightKg: Int = DEFAULT_WEIGHT_KG,
    val weightProvided: Boolean = false,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val maritalStatus: MaritalStatus? = null,
    val lastPeriodStartDate: LocalDate? = null,
    // Set when the user confirms the "I don't remember" dialog - satisfies the
    // mandatory requirement on this step without an exact date. Reset back to false the
    // moment a real date is picked (see ProfileSetupViewModel.onLastPeriodStartDateChanged).
    val lastPeriodUnknown: Boolean = false,
    // Free-text, like every other CyraTextField-backed field in the app (parsed to Int
    // best-effort at save time) - blank means "not provided", same as every other
    // optional field here.
    val averageCycleLengthDays: String = "",
    val averagePeriodDurationDays: String = "",
    // Unlike every other optional field, "Not sure" is a real, meaningful answer here
    // (not a null/unset sentinel) - it's always persisted, even if the user never
    // touches this control at all.
    val cycleRegularity: CycleRegularity = CycleRegularity.NotSure,
    val submitAttempted: Boolean = false,
    val isSubmitting: Boolean = false,
) {
    /** Name, Birthday and Last Period are the flow's mandatory, single-button steps. */
    val isPrimaryButtonEnabled: Boolean
        get() = when (step) {
            ProfileSetupStep.Name -> name.isNotBlank()
            ProfileSetupStep.Birthday -> dateOfBirth != null
            ProfileSetupStep.LastPeriod -> lastPeriodStartDate != null || lastPeriodUnknown
            else -> true
        }

    val primaryButtonLabelKey: String
        get() = if (step == ProfileSetupStep.AllSet) "profile_setup_button_start_journey" else "profile_setup_button_next"

    /**
     * Mandatory steps (and the completion screen) get one full-width button; every
     * optional step gets a "Skip" (left) + "Next" (right) pair instead, matching the
     * onboarding carousel's own Skip/Next convention.
     */
    val showDualButtons: Boolean
        get() = when (step) {
            ProfileSetupStep.Height, ProfileSetupStep.Weight, ProfileSetupStep.MaritalStatus, ProfileSetupStep.CycleInfo -> true
            else -> false
        }

    /** [ProfileSetupStep.Name] is the flow's entry point - there's nothing "behind" it to go back to. */
    val showBackButton: Boolean
        get() = step != ProfileSetupStep.Name

    val category: ProfileSetupCategory?
        get() = step.category
}
