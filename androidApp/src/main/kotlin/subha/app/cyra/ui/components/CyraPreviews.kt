package subha.app.cyra.ui.components

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes

/**
 * Apply this to every new composable/screen instead of a bare `@Preview`. Renders:
 *  - `@PreviewScreenSizes`: phone/foldable/tablet spread, so layouts get checked across
 *    all supported Android form factors, not just one default canvas.
 *  - `@PreviewFontScale`: several system font-scale variants (85%-200%). Because
 *    [subha.app.cyra.ui.theme.CyraTheme] locks `fontScale = 1f` app-wide, these variants
 *    should all render with IDENTICAL text size - if they don't, the font-scale lock is
 *    broken somewhere. This doubles as a regression check for that requirement.
 *
 * Note: stacked preview annotations produce the union of each annotation's variants, not
 * every combination of device x font-scale - e.g. "6 device sizes at default font scale"
 * + "6 font scales at default device," not all 36 combinations. That's sufficient for
 * what we need here.
 */
@PreviewScreenSizes
@PreviewFontScale
@Preview(name = "Default", showBackground = true)
annotation class CyraPreviews
