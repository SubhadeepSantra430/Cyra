package subha.app.cyra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay
import subha.app.cyra.ui.components.CyraPreviews
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
        enableEdgeToEdge()
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

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MILLIS)
        showSplash = false
    }

    Crossfade(targetState = showSplash, label = "splash-to-app") { isShowingSplash ->
        if (isShowingSplash) {
            CyraSplashScreen()
        } else {
            CyraAppFlow()
        }
    }
}

@Composable
private fun CyraAppFlow() {
    var onboardingComplete by remember { mutableStateOf(false) }

    Crossfade(targetState = onboardingComplete, label = "onboarding-to-app") { isComplete ->
        if (isComplete) {
            // TODO(Auth feature): replace with real navigation once auth/home exists.
            PlaceholderHomeScreen()
        } else {
            OnboardingScreen(onFinished = { onboardingComplete = true })
        }
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
