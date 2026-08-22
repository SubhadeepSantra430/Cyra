package subha.app.cyra.ui.profilesetup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.SharedFlow
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.feature.profilesetup.presentation.ProfileSetupEffect
import subha.app.cyra.ui.components.LocalCyraSnackbarController

/** Mirrors Auth's `HandleAuthEffects` exactly - see that function's doc comment. */
@Composable
fun HandleProfileSetupEffects(sideEffect: SharedFlow<ProfileSetupEffect>, onNavigate: (NavigationEvent) -> Unit) {
    val context = LocalContext.current
    val snackbarController = LocalCyraSnackbarController.current

    LaunchedEffect(sideEffect) {
        sideEffect.collect { effect ->
            when (effect) {
                is ProfileSetupEffect.Navigate -> onNavigate(effect.event)
                is ProfileSetupEffect.ShowError -> snackbarController.showError(context.getString(profileSetupMessageKeyToStringRes(effect.messageKey)))
                is ProfileSetupEffect.ShowSuccess -> snackbarController.showSuccess(context.getString(profileSetupMessageKeyToStringRes(effect.messageKey)))
            }
        }
    }
}
