package subha.app.cyra.feature.profilesetup.domain

/** [MaritalStatus.Widowed]'s row icon on both platforms is a plain person silhouette (a lone figure), [Single]'s is a heart (open to a relationship) - see the design canvas for the full icon set. */
enum class MaritalStatus(val messageKey: String) {
    Single("profile_setup_marital_status_single"),
    Married("profile_setup_marital_status_married"),
    Divorced("profile_setup_marital_status_divorced"),
    Widowed("profile_setup_marital_status_widowed"),
    PreferNotToSay("profile_setup_marital_status_prefer_not_to_say"),
}

/** How the user describes their own cycle - a 3-way segmented choice, not a text field. */
enum class CycleRegularity(val messageKey: String) {
    Regular("profile_setup_cycle_regularity_regular"),
    Irregular("profile_setup_cycle_regularity_irregular"),
    NotSure("profile_setup_cycle_regularity_not_sure"),
}

/**
 * Display unit for the height slider/field - the canonical stored value is always
 * centimeters ([subha.app.cyra.feature.profilesetup.presentation.ProfileSetupState.heightCm]);
 * this only changes what's shown and what the slider's live drag position converts
 * through (see `HeightConverter`).
 */
enum class HeightUnit { CM, FT_IN }

/** Display unit for the weight slider/field - canonical stored value is always kilograms. */
enum class WeightUnit { KG, LB }
