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
import kotlinx.coroutines.delay
import subha.app.cyra.ui.auth.AuthFlow
import subha.app.cyra.ui.components.CyraPreviews
import subha.app.cyra.ui.components.CyraSnackbarHost
import subha.app.cyra.ui.components.LocalCyraSnackbarController
import subha.app.cyra.ui.components.rememberCyraSnackbarController
import subha.app.cyra.ui.onboarding.OnboardingScreen
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
 * [CyraSplashScreen], then to onboarding, then to the (currently placeholder) app
 * content. Once real navigation/auth-state exists (Auth feature), the splash's timed
 * delay is replaced by "stay on splash until the auth-state check completes", and
 * onboarding's `onFinished` will route to real auth/home instead of the placeholder.
 */
@Composable
fun CyraRoot() {
    var showSplash by remember { mutableStateOf(true) }
    val snackbarController = rememberCyraSnackbarController()

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        showSplash = false
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
                    CyraAppFlow()
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

@Composable
private fun CyraAppFlow() {
    var onboardingComplete by remember { mutableStateOf(false) }
    // TODO(Auth feature): replace with a real session check (e.g. FirebaseClients.auth
    // .currentUser != null) once app-start persistence exists.
    var isAuthenticated by remember { mutableStateOf(false) }

    when {
        !onboardingComplete -> OnboardingScreen(onFinished = { onboardingComplete = true })
        !isAuthenticated -> AuthFlow(
            onAuthenticated = { isAuthenticated = true },
            onExitAuth = { onboardingComplete = false },
        )
        else -> PlaceholderHomeScreen()
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
