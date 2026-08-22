package subha.app.cyra.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import subha.app.cyra.ui.theme.CyraSuccess
import subha.app.cyra.ui.theme.CyraTheme

/** How long a [CyraSnackbarMessage] stays on screen before auto-dismissing. */
private const val SNACKBAR_DURATION_MILLIS = 3_500L

enum class CyraSnackbarType { SUCCESS, ERROR }

data class CyraSnackbarMessage(
    val id: Long,
    val text: String,
    val type: CyraSnackbarType,
)

/**
 * Owns the single "current message" queue-of-one for the whole app - any screen/
 * ViewModel-observer can call [showSuccess]/[showError] and it surfaces via whichever
 * [CyraSnackbarHost] is mounted (one, at the app root; see `MainActivity.CyraRoot`).
 * This is the piece that decouples "something happened" from "how it's drawn" - screens
 * never construct a [CyraSnackbar] themselves.
 */
@Stable
class CyraSnackbarController internal constructor(private val scope: CoroutineScope) {
    private val _currentMessage = MutableStateFlow<CyraSnackbarMessage?>(null)
    val currentMessage: StateFlow<CyraSnackbarMessage?> = _currentMessage.asStateFlow()

    private var nextId = 0L

    fun showSuccess(text: String) = show(text, CyraSnackbarType.SUCCESS)

    fun showError(text: String) = show(text, CyraSnackbarType.ERROR)

    private fun show(text: String, type: CyraSnackbarType) {
        val message = CyraSnackbarMessage(id = nextId++, text = text, type = type)
        _currentMessage.value = message
        scope.launch {
            delay(SNACKBAR_DURATION_MILLIS)
            dismiss(message.id)
        }
    }

    /** No-ops if [id] is no longer the message showing (a newer one already replaced it). */
    fun dismiss(id: Long) {
        if (_currentMessage.value?.id == id) _currentMessage.value = null
    }
}

@Composable
fun rememberCyraSnackbarController(): CyraSnackbarController {
    val scope = rememberCoroutineScope()
    return remember { CyraSnackbarController(scope) }
}

val LocalCyraSnackbarController = compositionLocalOf<CyraSnackbarController> {
    error("No CyraSnackbarController provided - wrap the app in CompositionLocalProvider(LocalCyraSnackbarController provides ...)")
}

/**
 * Mounted once at the app root ([subha.app.cyra.MainActivity]'s `CyraRoot`, top-aligned
 * just below the status bar), overlaid on top of whatever screen is currently showing.
 * Purely reactive to [CyraSnackbarController.currentMessage] - has no opinion about who's
 * producing messages.
 */
@Composable
fun CyraSnackbarHost(modifier: Modifier = Modifier, controller: CyraSnackbarController = LocalCyraSnackbarController.current) {
    val message by controller.currentMessage.collectAsState()

    Box(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = message != null,
            // Slides down from above (negative initial offset) since the host sits at
            // the top of the screen now - a positive offset would slide it in from
            // further down the screen, through already-visible content.
            enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it / 2 },
            exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { -it / 2 },
        ) {
            message?.let { CyraSnackbar(text = it.text, type = it.type, modifier = Modifier.fillMaxWidth()) }
        }
    }
}

/**
 * The reusable, decoupled snackbar itself - a pure function of (text, type). Green
 * background + tick for success, red background + cross for error; same font/corner-
 * radius/spacing language as the rest of the app ([CyraTextField], [CyraPrimaryButton]).
 * Knows nothing about controllers, flows, or auto-dismiss timing, so it can be dropped
 * into a preview, a different host, or a future non-Auth feature with zero changes.
 */
@Composable
fun CyraSnackbar(text: String, type: CyraSnackbarType, modifier: Modifier = Modifier) {
    val backgroundColor = when (type) {
        CyraSnackbarType.SUCCESS -> CyraSuccess
        CyraSnackbarType.ERROR -> MaterialTheme.colorScheme.error
    }
    val contentColor = Color.White

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconModifier = Modifier.size(18.dp)
        when (type) {
            CyraSnackbarType.SUCCESS -> TickIcon(color = contentColor, modifier = iconModifier)
            CyraSnackbarType.ERROR -> CrossIcon(color = contentColor, modifier = iconModifier)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}

@CyraPreviews
@Composable
private fun CyraSnackbarSuccessPreview() {
    CyraTheme {
        CyraSnackbar(text = "Account created successfully!", type = CyraSnackbarType.SUCCESS)
    }
}

@CyraPreviews
@Composable
private fun CyraSnackbarErrorPreview() {
    CyraTheme {
        CyraSnackbar(text = "Incorrect email or password", type = CyraSnackbarType.ERROR)
    }
}
