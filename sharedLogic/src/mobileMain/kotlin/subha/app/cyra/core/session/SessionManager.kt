package subha.app.cyra.core.session

import subha.app.cyra.core.datastore.AppSettings
import subha.app.cyra.feature.auth.data.AuthRepository

/**
 * The one place cold-start routing gets decided - local-only and offline-safe by
 * design (checks [AppSettings]'s cached flags + Firebase Auth's own persisted session,
 * never a network call), matching the app's offline-first stance elsewhere.
 *
 * TODO(follow-up, not this pass): on iOS specifically, a Keychain-persisted Firebase
 * Auth session can survive an app reinstall while local Settings/Room are wiped fresh -
 * so a returning user who genuinely finished profile setup could see
 * [AppStartupDestination.NeedsProfileSetup] again here instead of [AppStartupDestination
 * .Home], even though `ProfileRepository`'s `ProfileDocument.profileSetupCompleted`
 * already has the real answer sitting in Firestore. Deliberately not reconciled against
 * Firestore yet - that would mean a live network read on every ambiguous cold start,
 * trading this class's fixed, predictable latency for the sake of a narrow
 * uninstall+reinstall edge case whose worst case today is just re-answering a few
 * non-destructive questions. Same category of deferred decision as `// TODO(Auth
 * feature)` markers elsewhere in this app.
 */
class SessionManager(
    private val authRepository: AuthRepository,
    private val appSettings: AppSettings,
) {
    suspend fun determineStartupDestination(): AppStartupDestination {
        val userId = authRepository.currentUserId
        return when {
            !appSettings.isOnboarded -> AppStartupDestination.NeedsOnboardingCarousel
            userId == null -> AppStartupDestination.NeedsAuth
            appSettings.isProfileSetupCompleted -> AppStartupDestination.Home
            else -> AppStartupDestination.NeedsProfileSetup(userId)
        }
    }
}
