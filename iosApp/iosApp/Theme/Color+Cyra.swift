import SwiftUI

/// The "Serene Radiance" palette - confirmed to be Tailwind CSS's stock `violet`/`gray`
/// ramps (the 4 given hex values are exact matches: violet-500, violet-300, gray-800,
/// gray-50), mirrored byte-for-byte from the Android side (`ui/theme/Color.kt`). Kept as
/// a hardcoded Swift extension rather than an Asset Catalog ColorSet so it stays
/// greppable/diffable against the Kotlin values instead of drifting silently in JSON.
private func rgb(_ r: Int, _ g: Int, _ b: Int) -> Color {
    Color(red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255)
}

extension Color {
    // Primary - violet-500
    static let cyraPrimary = rgb(0x8B, 0x5C, 0xF6)
    static let cyraPrimaryContainer = rgb(0xED, 0xE9, 0xFE) // violet-100
    static let cyraOnPrimary = Color.white

    // Secondary - violet-300
    static let cyraSecondary = rgb(0xC4, 0xB5, 0xFD)
    static let cyraOnSecondary = rgb(0x1F, 0x29, 0x37) // gray-800

    // Tertiary - gray-800 (also the app's primary text color)
    static let cyraTertiary = rgb(0x1F, 0x29, 0x37)
    static let cyraOnTertiary = Color.white

    // Neutral - gray-50
    static let cyraBackground = rgb(0xF9, 0xFA, 0xFB)
    static let cyraSurface = Color.white
    static let cyraOnSurface = rgb(0x1F, 0x29, 0x37) // gray-800
    static let cyraOnSurfaceVariant = rgb(0x6B, 0x72, 0x80) // gray-500
    static let cyraOutline = rgb(0xD1, 0xD5, 0xDB) // gray-300

    // Semantic - not part of the given palette, conventional placeholder
    static let cyraError = rgb(0xEF, 0x44, 0x44) // red-500
    static let cyraOnError = Color.white
}
