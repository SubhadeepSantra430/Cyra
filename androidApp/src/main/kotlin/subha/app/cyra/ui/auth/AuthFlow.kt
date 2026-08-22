package subha.app.cyra.ui.auth

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import subha.app.cyra.core.presentation.NavigationEvent

/**
 * Owns which Auth screen is showing and translates each screen's [NavigationEvent]s
 * into either a destination change or one of the two flow-level callbacks - mirrors
 * `CyraAppFlow`'s existing `Crossfade`-over-local-state pattern (see MainActivity.kt),
 * since no real navigation graph exists anywhere in the app yet.
 */
@Composable
fun AuthFlow(
    onAuthenticated: () -> Unit,
    onExitAuth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var destination by remember { mutableStateOf<AuthDestination>(AuthDestination.Login) }

    fun handleNavigation(event: NavigationEvent) {
        when (event) {
            NavigationEvent.NavigateToSignup -> destination = AuthDestination.Signup
            NavigationEvent.NavigateToForgotPassword -> destination = AuthDestination.ForgotPassword
            NavigationEvent.NavigateToLogin -> destination = AuthDestination.Login
            NavigationEvent.NavigateToHome -> onAuthenticated()
            NavigationEvent.NavigateBack -> when (destination) {
                // Login is the flow's entry point - there's nothing "behind" it inside
                // Auth yet, so back exits the whole flow (back to onboarding).
                AuthDestination.Login -> onExitAuth()
                AuthDestination.Signup -> destination = AuthDestination.Login
                AuthDestination.ForgotPassword -> destination = AuthDestination.Login
            }
            is NavigationEvent.NavigateToOnboarding -> Unit
        }
    }

    Crossfade(targetState = destination, label = "auth-destination", modifier = modifier) { current ->
        when (current) {
            AuthDestination.Login -> LoginScreen(onNavigate = ::handleNavigation)
            AuthDestination.Signup -> SignupScreen(onNavigate = ::handleNavigation)
            AuthDestination.ForgotPassword -> ForgotPasswordScreen(onNavigate = ::handleNavigation)
        }
    }
}
