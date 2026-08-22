import SwiftUI

/// The title+subtitle block every step shares, wrapping that step's own unique body -
/// mirrors Android's `ProfileSetupStepScaffold.kt`.
struct ProfileSetupStepScaffold<Content: View>: View {
    let title: String
    let subtitle: String
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(title)
                .font(CyraFont.headlineMedium())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            Text(subtitle)
                .font(CyraFont.bodyMedium())
                .foregroundColor(.cyraOnSurfaceVariant)
            Spacer().frame(height: 24)
            content()
        }
    }
}
