package subha.app.cyra.feature.auth.presentation

import subha.app.cyra.core.presentation.NavigationEvent

/**
 * One shared one-shot effect type for every Auth ViewModel (Login/Signup/ForgotPassword)
 * - all three need exactly the same things, so this avoids three near-identical bespoke
 * effect interfaces. Wraps [NavigationEvent] per that type's own doc comment ("each
 * feature's SideEffect type typically wraps or extends these").
 *
 * [ShowError]/[ShowSuccess] are deliberately just a message key each - neither knows
 * about snackbars, string resources, or any other UI concern. Both platforms route them
 * into the same global snackbar (`HandleAuthEffects` on Android, `handle(_:)` +
 * `CyraSnackbarQueue` on iOS) rather than each screen rendering its own inline banner.
 */
sealed interface AuthEffect {
    data class Navigate(val event: NavigationEvent) : AuthEffect
    data class ShowError(val messageKey: String) : AuthEffect
    data class ShowSuccess(val messageKey: String) : AuthEffect
}
