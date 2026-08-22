package subha.app.cyra.feature.profilesetup.presentation

import kotlinx.datetime.LocalDate
import subha.app.cyra.feature.profilesetup.domain.CycleRegularity
import subha.app.cyra.feature.profilesetup.domain.HeightUnit
import subha.app.cyra.feature.profilesetup.domain.MaritalStatus
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupCategory
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupStep
import subha.app.cyra.feature.profilesetup.domain.WeightUnit

private const val DEFAULT_HEIGHT_CM = 160
private const val DEFAULT_WEIGHT_KG = 60

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
    // Free-text, like every other CyraTextField-backed field in the app (parsed to Int
    // best-effort at save time) - blank means "not provided", same as every other
    // optional field here.
    val averageCycleLengthDays: String = "",
    val averagePeriodDurationDays: String = "",
    val cycleRegularity: CycleRegularity? = null,
    val submitAttempted: Boolean = false,
    val isSubmitting: Boolean = false,
) {
    /** Only [ProfileSetupStep.Name] and [ProfileSetupStep.Birthday] are mandatory. */
    val isPrimaryButtonEnabled: Boolean
        get() = when (step) {
            ProfileSetupStep.Name -> name.isNotBlank()
            ProfileSetupStep.Birthday -> dateOfBirth != null
            else -> true
        }

    val primaryButtonLabelKey: String
        get() = when (step) {
            ProfileSetupStep.Name, ProfileSetupStep.Birthday -> "profile_setup_button_next"
            ProfileSetupStep.AllSet -> "profile_setup_button_start_journey"
            else -> "profile_setup_button_skip"
        }

    /** [ProfileSetupStep.Name] is the flow's entry point - there's nothing "behind" it to go back to. */
    val showBackButton: Boolean
        get() = step != ProfileSetupStep.Name

    val category: ProfileSetupCategory?
        get() = step.category
}
