package subha.app.cyra.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.SharedFlow
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.feature.auth.presentation.AuthEffect
import subha.app.cyra.ui.components.LocalCyraSnackbarController

/**
 * The one place [AuthEffect] gets interpreted, shared by `LoginScreen`, `SignupScreen`
 * and `ForgotPasswordScreen` - previously each screen duplicated this `LaunchedEffect`
 * and kept its own `errorMessageKey` state rendered as an inline `Text` banner. Now
 * `ShowError`/`ShowSuccess` both route into the single global [LocalCyraSnackbarController]
 * instead, so no screen renders its own error/success UI anymore.
 */
@Composable
fun HandleAuthEffects(sideEffect: SharedFlow<AuthEffect>, onNavigate: (NavigationEvent) -> Unit) {
    val context = LocalContext.current
    val snackbarController = LocalCyraSnackbarController.current

    LaunchedEffect(sideEffect) {
        sideEffect.collect { effect ->
            when (effect) {
                is AuthEffect.Navigate -> onNavigate(effect.event)
                is AuthEffect.ShowError -> snackbarController.showError(context.getString(messageKeyToStringRes(effect.messageKey)))
                is AuthEffect.ShowSuccess -> snackbarController.showSuccess(context.getString(messageKeyToStringRes(effect.messageKey)))
            }
        }
    }
}
