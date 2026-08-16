package subha.app.cyra.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val CyraLightColorScheme = lightColorScheme(
    primary = CyraPrimary,
    onPrimary = CyraOnPrimary,
    primaryContainer = CyraPrimaryContainer,
    secondary = CyraSecondary,
    onSecondary = CyraOnSecondary,
    tertiary = CyraTertiary,
    onTertiary = CyraOnTertiary,
    background = CyraBackground,
    onBackground = CyraOnSurface,
    surface = CyraSurface,
    onSurface = CyraOnSurface,
    surfaceVariant = CyraBackground,
    onSurfaceVariant = CyraOnSurfaceVariant,
    outline = CyraOutline,
    error = CyraError,
    onError = CyraOnError,
)

/**
 * The single wrap point for the whole app (applied once, in `CyraRoot()`). Two things
 * happen here and nowhere else:
 *  1. The Cyra color scheme + Manrope typography are applied via [MaterialTheme].
 *  2. Text is locked against the system's accessibility font-scale setting.
 *
 * On (2): Compose's `sp` unit scales with `fontScale` (the system font-size/accessibility
 * setting) by default - that's normally correct accessibility behavior, but this app's
 * design explicitly calls for fixed typography that never changes size. The standard,
 * correct way to disable it is to override [LocalDensity] with the real screen `density`
 * (so `dp` sizing is untouched) but a hardcoded `fontScale = 1f`. Doing it here, once,
 * means no individual screen/component can get this wrong.
 */
@Composable
fun CyraTheme(content: @Composable () -> Unit) {
    val fixedFontScaleDensity = Density(
        density = LocalDensity.current.density,
        fontScale = 1f,
    )
    CompositionLocalProvider(LocalDensity provides fixedFontScaleDensity) {
        MaterialTheme(
            colorScheme = CyraLightColorScheme,
            typography = CyraTypography,
            content = content,
        )
    }
}
