package subha.app.cyra

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.core.session.AppStartupDestination
import subha.app.cyra.core.session.AppStartupViewModel
import subha.app.cyra.ui.auth.AuthFlow
import subha.app.cyra.ui.components.CyraPreviews
import subha.app.cyra.ui.components.CyraSnackbarHost
import subha.app.cyra.ui.components.LocalCyraSnackbarController
import subha.app.cyra.ui.components.rememberCyraSnackbarController
import subha.app.cyra.ui.onboarding.OnboardingScreen
import subha.app.cyra.ui.profilesetup.ProfileSetupScreen
import subha.app.cyra.ui.splash.CyraSplashScreen
import subha.app.cyra.ui.theme.CyraTheme

private const val SPLASH_DURATION_MILLIS = 1200L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate() - this is what shows Theme.App.Starting's
        // system splash (values/themes.xml) instantly at cold start. installSplashScreen()
        // is a member extension function on SplashScreen.Companion - imported this way, it's
        // callable bare on `this` (the Activity), not as SplashScreen.installSplashScreen(this).
        installSplashScreen()
        // CyraTheme is always the light color scheme (no dark variant exists yet - see
        // ui/theme/Theme.kt) - forcing SystemBarStyle.light() keeps status/nav bar icons
        // dark (visible against our light background) regardless of the *system's* dark
        // mode setting, instead of enableEdgeToEdge()'s default (which follows the system
        // setting and would otherwise draw light, near-invisible icons on this app).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        setContent {
            CyraTheme {
                CyraRoot()
            }
        }
    }
}

/**
 * Handles the handoff from the system splash to our own full-screen branded
 * [CyraSplashScreen], then to the real app flow - [AppStartupViewModel] resolves once,
 * offline-safe, which of onboarding/auth/profile-setup-resume/home a cold start should
 * land on (see that class and [AppStartupDestination]). The brand splash stays up until
 * *both* its own minimum duration AND that resolution have completed, whichever is
 * later - a slow resolution no longer risks a jarring flash of the wrong screen.
 */
@Composable
fun CyraRoot() {
    val startupViewModel: AppStartupViewModel = koinViewModel()
    val destination by startupViewModel.destination.collectAsStateWithLifecycle()
    var minimumSplashDurationElapsed by remember { mutableStateOf(false) }
    val showSplash = !minimumSplashDurationElapsed || destination == null
    val snackbarController = rememberCyraSnackbarController()

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        minimumSplashDurationElapsed = true
    }

    // Provided once, here at the root, so any screen further down can show a global
    // snackbar (see subha.app.cyra.ui.components.CyraSnackbar) without threading a
    // controller reference through every navigation call.
    CompositionLocalProvider(LocalCyraSnackbarController provides snackbarController) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = showSplash, label = "splash-to-app") { isShowingSplash ->
                if (isShowingSplash) {
                    CyraSplashScreen()
                } else {
                    // Safe: showSplash is only false once destination has resolved.
                    CyraAppFlow(initialDestination = destination!!)
                }
            }
            CyraSnackbarHost(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp),
            )
        }
    }
}

/**
 * [initialDestination] is [AppStartupViewModel]'s one-shot cold-start decision;
 * [override] is local, in-session state for the transitions that happen *afterward*
 * (onboarding finishes, auth completes, profile setup finishes) - the startup check
 * itself only needs to run once per launch.
 */
@Composable
private fun CyraAppFlow(
    initialDestination: AppStartupDestination,
    startupViewModel: AppStartupViewModel = koinViewModel(),
) {
    var override by remember { mutableStateOf<AppStartupDestination?>(null) }

    when (val destination = override ?: initialDestination) {
        AppStartupDestination.NeedsOnboardingCarousel -> OnboardingScreen(onFinished = {
            startupViewModel.markOnboardingCarouselSeen()
            override = AppStartupDestination.NeedsAuth
        })
        is AppStartupDestination.NeedsProfileSetup -> ProfileSetupScreen(
            userId = destination.userId,
            onNavigate = { event ->
                if (event is NavigationEvent.NavigateToHome) override = AppStartupDestination.Home
            },
        )
        AppStartupDestination.NeedsAuth -> AuthFlow(
            onAuthenticated = { override = AppStartupDestination.Home },
            onExitAuth = { override = AppStartupDestination.NeedsOnboardingCarousel },
            onNeedsProfileSetup = { userId -> override = AppStartupDestination.NeedsProfileSetup(userId) },
        )
        AppStartupDestination.Home -> PlaceholderHomeScreen()
    }
}

@Composable
fun PlaceholderHomeScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cyra - architecture scaffolding in place")
        }
    }
}

@CyraPreviews
@Composable
private fun PlaceholderHomeScreenPreview() {
    CyraTheme {
        PlaceholderHomeScreen()
    }
}
