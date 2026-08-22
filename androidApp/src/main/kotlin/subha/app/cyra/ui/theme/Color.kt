package subha.app.cyra.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * The "Serene Radiance" palette - confirmed to be Tailwind CSS's stock `violet`/`gray`
 * ramps (the 4 given hex values are exact matches: violet-500, violet-300, gray-800,
 * gray-50), so the rest of the ramp is filled in from the same source rather than
 * invented. `error`/`onError` weren't specified - a conventional Tailwind red-500 is
 * used as a placeholder, trivial to swap if a different one is ever given.
 */

// Primary - violet-500
val CyraPrimary = Color(0xFF8B5CF6)
val CyraPrimaryContainer = Color(0xFFEDE9FE) // violet-100
val CyraOnPrimary = Color(0xFFFFFFFF)

// Secondary - violet-300
val CyraSecondary = Color(0xFFC4B5FD)
val CyraOnSecondary = Color(0xFF1F2937) // gray-800 reads better on light violet than white

// Tertiary - gray-800 (also doubles as the app's primary text color)
val CyraTertiary = Color(0xFF1F2937)
val CyraOnTertiary = Color(0xFFFFFFFF)

// Neutral - gray-50
val CyraBackground = Color(0xFFF9FAFB)
val CyraSurface = Color(0xFFFFFFFF)
val CyraOnSurface = Color(0xFF1F2937) // gray-800
val CyraOnSurfaceVariant = Color(0xFF6B7280) // gray-500, muted/secondary text
val CyraOutline = Color(0xFFD1D5DB) // gray-300

// Semantic - not part of the given palette, conventional placeholder
val CyraError = Color(0xFFEF4444) // red-500
val CyraOnError = Color(0xFFFFFFFF)
val CyraSuccess = Color(0xFF22C55E) // green-500
val CyraOnSuccess = Color(0xFFFFFFFF)
