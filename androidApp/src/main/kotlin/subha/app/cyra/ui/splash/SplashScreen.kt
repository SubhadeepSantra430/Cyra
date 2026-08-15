package subha.app.cyra.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import subha.app.cyra.R

/**
 * The full-screen branded splash - shown immediately after the system splash screen
 * (see `Theme.App.Starting` in values/themes.xml) hands off to the app's first Compose
 * frame. The system splash API only supports a centered icon on a solid background;
 * this is what actually delivers the full-bleed look, and gives full control over
 * duration/transition (see `CyraRoot` in MainActivity.kt). Named `CyraSplashScreen`
 * (not `SplashScreen`) to avoid colliding with `androidx.core.splashscreen.SplashScreen`.
 */
@Composable
fun CyraSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.splash_logo),
            contentDescription = null,
            modifier = Modifier.size(180.dp),
        )
    }
}

@Preview
@Composable
private fun CyraSplashScreenPreview() {
    CyraSplashScreen()
}
