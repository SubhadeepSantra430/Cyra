package subha.app.cyra.feature.auth.presentation

import subha.app.cyra.core.presentation.NavigationEvent

/**
 * One shared one-shot effect type for every Auth ViewModel (Login/Signup/ForgotPassword)
 * - all three need exactly the same two things, so this avoids three near-identical
 * bespoke effect interfaces. Wraps [NavigationEvent] per that type's own doc comment
 * ("each feature's SideEffect type typically wraps or extends these").
 */
sealed interface AuthEffect {
    data class Navigate(val event: NavigationEvent) : AuthEffect
    data class ShowError(val messageKey: String) : AuthEffect
}
