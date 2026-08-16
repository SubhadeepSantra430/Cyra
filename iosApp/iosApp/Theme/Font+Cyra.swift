import SwiftUI

/// Manrope, mirroring `ui/theme/Typography.kt` on Android. PostScript names below were
/// confirmed by parsing the actual TTF `name` tables directly (Font.custom keys on the
/// PostScript name, not the filename) - don't guess these from the filenames.
///
/// Every style here uses `Font.custom(_:fixedSize:)`, NOT the `size:` overload. Apple
/// documents `fixedSize:` as immune to Dynamic Type at every accessibility level
/// (including the Larger Accessibility Sizes range) - a stronger guarantee than just
/// avoiding `relativeTo:`. This is the one place font sizing happens; no screen should
/// build its own `Font.custom` call.
enum CyraFont {
    static func displayLarge() -> Font { .custom("Manrope-ExtraBold", fixedSize: 36) }
    static func displayMedium() -> Font { .custom("Manrope-ExtraBold", fixedSize: 32) }
    static func displaySmall() -> Font { .custom("Manrope-Bold", fixedSize: 28) }

    static func headlineLarge() -> Font { .custom("Manrope-Bold", fixedSize: 28) }
    static func headlineMedium() -> Font { .custom("Manrope-Bold", fixedSize: 24) }
    static func headlineSmall() -> Font { .custom("Manrope-SemiBold", fixedSize: 20) }

    static func titleLarge() -> Font { .custom("Manrope-SemiBold", fixedSize: 18) }
    static func titleMedium() -> Font { .custom("Manrope-Medium", fixedSize: 16) }
    static func titleSmall() -> Font { .custom("Manrope-Medium", fixedSize: 14) }

    static func bodyLarge() -> Font { .custom("Manrope-Regular", fixedSize: 16) }
    static func bodyMedium() -> Font { .custom("Manrope-Regular", fixedSize: 14) }
    static func bodySmall() -> Font { .custom("Manrope-Regular", fixedSize: 12) }

    static func labelLarge() -> Font { .custom("Manrope-SemiBold", fixedSize: 14) }
    static func labelMedium() -> Font { .custom("Manrope-Medium", fixedSize: 12) }
    static func labelSmall() -> Font { .custom("Manrope-Medium", fixedSize: 11) }
}
