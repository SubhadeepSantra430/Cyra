package subha.app.cyra.ui.profilesetup

import androidx.annotation.StringRes
import subha.app.cyra.R

/**
 * Maps a `messageKey` from `ProfileSetupValidators`/`ProfileSetupEffect`, or a
 * `ProfileSetupCategory`/`MaritalStatus`/`CycleRegularity`'s `messageKey`, to its
 * Android string resource - same purpose as Auth's `messageKeyToStringRes`, kept as a
 * separate function (rather than extending that one) since these two feature's key
 * spaces are unrelated and shouldn't need to know about each other.
 */
@StringRes
fun profileSetupMessageKeyToStringRes(key: String): Int = when (key) {
    "profile_setup_category_about_you" -> R.string.profile_setup_category_about_you
    "profile_setup_category_your_cycle" -> R.string.profile_setup_category_your_cycle
    "profile_setup_button_next" -> R.string.profile_setup_button_next
    "profile_setup_button_skip" -> R.string.profile_setup_button_skip
    "profile_setup_button_start_journey" -> R.string.profile_setup_button_start_journey
    "profile_setup_error_name_required" -> R.string.profile_setup_error_name_required
    "profile_setup_error_dob_required" -> R.string.profile_setup_error_dob_required
    "profile_setup_success" -> R.string.profile_setup_success
    "profile_setup_error_save_failed" -> R.string.profile_setup_error_save_failed
    "profile_setup_marital_status_single" -> R.string.profile_setup_marital_status_single
    "profile_setup_marital_status_married" -> R.string.profile_setup_marital_status_married
    "profile_setup_marital_status_divorced" -> R.string.profile_setup_marital_status_divorced
    "profile_setup_marital_status_widowed" -> R.string.profile_setup_marital_status_widowed
    "profile_setup_marital_status_prefer_not_to_say" -> R.string.profile_setup_marital_status_prefer_not_to_say
    "profile_setup_cycle_regularity_regular" -> R.string.profile_setup_cycle_regularity_regular
    "profile_setup_cycle_regularity_irregular" -> R.string.profile_setup_cycle_regularity_irregular
    "profile_setup_cycle_regularity_not_sure" -> R.string.profile_setup_cycle_regularity_not_sure
    else -> R.string.auth_error_generic
}
