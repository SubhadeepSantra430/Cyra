package subha.app.cyra.feature.profilesetup.domain

/**
 * The category a step belongs to, shown as plain text between the back button and the
 * step counter (e.g. "About You") - `null` on [ProfileSetupStep.AllSet], which is a
 * completion screen rather than a form category.
 */
enum class ProfileSetupCategory(val messageKey: String) {
    ABOUT_YOU("profile_setup_category_about_you"),
    YOUR_CYCLE("profile_setup_category_your_cycle"),
}

/**
 * One screen in the post-signup profile-setup flow, in display order (ordinal ==
 * display order, relied on by [next]/[previous]). Deliberately a single flow driven by
 * one [subha.app.cyra.feature.profilesetup.presentation.ProfileSetupViewModel] - unlike
 * Auth's three separate screens/ViewModels, every step here contributes to the same
 * eventual profile write, so one accumulating state object is the simpler shape.
 *
 * [Name] and [Birthday] are mandatory (button reads "Next", disabled until valid);
 * every other step is optional (button always reads "Skip" and stays enabled) - see
 * `ProfileSetupState.isPrimaryButtonEnabled`/`primaryButtonLabelKey`.
 */
enum class ProfileSetupStep(val stepNumber: Int, val category: ProfileSetupCategory?) {
    Name(1, ProfileSetupCategory.ABOUT_YOU),
    Birthday(2, ProfileSetupCategory.ABOUT_YOU),
    Height(3, ProfileSetupCategory.ABOUT_YOU),
    Weight(4, ProfileSetupCategory.ABOUT_YOU),
    MaritalStatus(5, ProfileSetupCategory.ABOUT_YOU),
    LastPeriod(6, ProfileSetupCategory.YOUR_CYCLE),
    CycleInfo(7, ProfileSetupCategory.YOUR_CYCLE),
    AllSet(8, null),
    ;

    /** `null` past the last step - the caller (ViewModel) treats that as "submit". */
    fun next(): ProfileSetupStep? = entries.getOrNull(ordinal + 1)

    /** `null` on the first step - [Name] hides the back button entirely, see `ProfileSetupState.showBackButton`. */
    fun previous(): ProfileSetupStep? = entries.getOrNull(ordinal - 1)

    companion object {
        val TOTAL_STEPS = entries.size
    }
}
