package subha.app.cyra.core.session

/**
 * Where a cold start should land, decided once by [SessionManager] - replaces the three
 * independent `remember`/`@State` booleans (`onboardingComplete`, `isAuthenticated`,
 * `profileSetupUserId`) `MainActivity.kt`'s `CyraAppFlow`/`iOSApp.swift`'s
 * `CyraRootView` used to carry, which always reset to their defaults on every launch.
 *
 * [NeedsProfileSetup] carries only the [userId] - `ProfileSetupViewModel` resumes its
 * own [subha.app.cyra.feature.profilesetup.domain.ProfileSetupStep] from its own
 * offline-first draft (see `ProfileSetupDraftRepository`); this type doesn't need to
 * know which step.
 */
sealed interface AppStartupDestination {
    data object NeedsOnboardingCarousel : AppStartupDestination
    data object NeedsAuth : AppStartupDestination
    data class NeedsProfileSetup(val userId: String) : AppStartupDestination
    data object Home : AppStartupDestination
}
