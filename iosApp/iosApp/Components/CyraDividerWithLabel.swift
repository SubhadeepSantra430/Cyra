import SwiftUI

/// The "or continue with" divider row - mirrors Android's `CyraDividerWithLabel.kt`.
struct CyraDividerWithLabel: View {
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Rectangle().fill(Color.cyraOutline).frame(height: 1)
            Text(text)
                .font(CyraFont.bodyMedium())
                .foregroundColor(.cyraOnSurfaceVariant)
                .fixedSize()
            Rectangle().fill(Color.cyraOutline).frame(height: 1)
        }
    }
}

#Preview {
    CyraDividerWithLabel(text: "or continue with")
        .padding(24)
        .cyraThemed()
}
