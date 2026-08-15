package subha.app.cyra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Placeholder root Composable - androidApp now depends on `:sharedLogic` directly
 * (not `:sharedUI`/Compose Multiplatform, which is parked). Replaced with the real
 * navigation host + AppLockGate once the Auth feature (first in MVP order) is built.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
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
