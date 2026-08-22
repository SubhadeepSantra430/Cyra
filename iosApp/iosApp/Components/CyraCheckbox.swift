import SwiftUI

/// A round checkbox, matching the reference design's circular Terms-agreement toggle -
/// mirrors Android's `CyraCheckbox.kt`.
struct CyraCheckbox: View {
    let checked: Bool
    let onCheckedChange: (Bool) -> Void

    var body: some View {
        Button(action: { onCheckedChange(!checked) }) {
            Circle()
                .fill(checked ? Color.cyraPrimary : Color.cyraSurface)
                .overlay(Circle().stroke(checked ? Color.cyraPrimary : Color.cyraOutline, lineWidth: 1.5))
                .frame(width: 22, height: 22)
                .overlay {
                    if checked {
                        Image(systemName: "checkmark")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(.white)
                    }
                }
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    HStack(spacing: 12) {
        CyraCheckbox(checked: false, onCheckedChange: { _ in })
        CyraCheckbox(checked: true, onCheckedChange: { _ in })
    }
    .padding(24)
    .cyraThemed()
}
