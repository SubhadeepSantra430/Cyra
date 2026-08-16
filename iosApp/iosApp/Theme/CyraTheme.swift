import SwiftUI

/// Applied once, at the app root (see `CyraRootView` in `iOSApp.swift`) - mirrors
/// Android's `CyraTheme()` composable. Every text style already uses
/// `Font.custom(_:fixedSize:)` (see `Font+Cyra.swift`), which alone is documented as
/// immune to Dynamic Type; `.dynamicTypeSize(.large)` here is defense-in-depth so
/// nothing in the accessibility settings can affect layout-level sizing either.
extension View {
    func cyraThemed() -> some View {
        self
            .dynamicTypeSize(.large)
            .background(Color.cyraBackground.ignoresSafeArea())
    }
}
