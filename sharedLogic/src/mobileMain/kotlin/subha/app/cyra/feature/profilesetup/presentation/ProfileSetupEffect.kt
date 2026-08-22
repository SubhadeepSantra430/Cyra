package subha.app.cyra.feature.profilesetup.presentation

import subha.app.cyra.core.presentation.NavigationEvent

/**
 * Mirrors Auth's `AuthEffect` shape exactly (see that type's doc comment) - both
 * platforms route [ShowError]/[ShowSuccess] into the same global snackbar
 * (`HandleAuthEffects`-equivalent handling on Android, `handleAuthEffect`-equivalent on
 * iOS; see `ui/profilesetup/ProfileSetupEffectHandler.kt` /
 * `ProfileSetup/ProfileSetupEffectHandler.swift`).
 */
sealed interface ProfileSetupEffect {
    data class Navigate(val event: NavigationEvent) : ProfileSetupEffect
    data class ShowError(val messageKey: String) : ProfileSetupEffect
    data class ShowSuccess(val messageKey: String) : ProfileSetupEffect
}
