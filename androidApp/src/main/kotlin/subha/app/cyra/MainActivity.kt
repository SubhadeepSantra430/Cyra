package subha.app.cyra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay
import subha.app.cyra.ui.splash.CyraSplashScreen

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
            CyraRoot()
        }
    }
}

/**
 * Handles the handoff from the system splash to our own full-screen branded [SplashScreen],
 * then to the real app content. Once real navigation/auth-state exists (Auth feature),
 * this timed delay is replaced by "stay on splash until the auth-state check completes".
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
            CyraApp()
        }
    }
}

@Composable
fun CyraApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cyra - architecture scaffolding in place")
            }
        }
    }
}

@Preview
@Composable
private fun CyraAppPreview() {
    CyraApp()
}
