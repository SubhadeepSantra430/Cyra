package subha.app.cyra.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import subha.app.cyra.R

data class OnboardingPageData(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
)

/**
 * The illustrations already include their own page-specific context card (calendar/
 * fertile-window, lock/privacy, progress chart) baked into the artwork - cropped
 * directly from the reference design rather than rebuilt as separate overlay UI, since
 * reconstructing a mini calendar/chart widget in code for a decorative background
 * element isn't worth the effort versus using the real exported art.
 */
val onboardingPages = listOf(
    OnboardingPageData(
        imageRes = R.drawable.onboarding_illustration_1,
        titleRes = R.string.onboarding_page1_title,
        descriptionRes = R.string.onboarding_page1_description,
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_illustration_2,
        titleRes = R.string.onboarding_page2_title,
        descriptionRes = R.string.onboarding_page2_description,
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_illustration_3,
        titleRes = R.string.onboarding_page3_title,
        descriptionRes = R.string.onboarding_page3_description,
    ),
)
