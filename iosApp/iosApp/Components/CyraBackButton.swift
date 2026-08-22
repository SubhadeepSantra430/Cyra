import SwiftUI

/// The floating circular back button from the reference design - mirrors Android's
/// `CyraBackButton.kt`. Uses the native `chevron.left` SF Symbol, matching how
/// `CyraPrimaryButtonStyle` already uses `chevron.right`.
struct CyraBackButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "chevron.left")
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(.cyraOnSurface)
                .frame(width: 40, height: 40)
                .background(Circle().fill(Color.cyraSurface))
                .shadow(color: .black.opacity(0.08), radius: 4, y: 2)
        }
        .accessibilityLabel(String(localized: "auth_back_content_description"))
    }
}

#Preview {
    CyraBackButton(action: {})
        .padding(24)
        .cyraThemed()
}
