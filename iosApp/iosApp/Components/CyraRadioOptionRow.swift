import SwiftUI

/// A full-row single-select option - leading SF Symbol, label, trailing radio dot -
/// mirrors Android's `CyraRadioOptionRow.kt`. Used for marital status. Same 16pt corner
/// radius as `CyraTextField` for visual consistency across the flow's controls.
struct CyraRadioOptionRow: View {
    let systemImage: String
    let label: String
    let selected: Bool
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            HStack {
                HStack(spacing: 12) {
                    Image(systemName: systemImage)
                        .foregroundColor(.cyraOnSurfaceVariant)
                        .frame(width: 20)
                    Text(label)
                        .font(CyraFont.titleMedium())
                        .foregroundColor(.cyraOnSurface)
                }
                Spacer()
                ZStack {
                    Circle()
                        .stroke(selected ? Color.cyraPrimary : Color.cyraOutline, lineWidth: 1.6)
                        .frame(width: 20, height: 20)
                    if selected {
                        Circle()
                            .fill(Color.cyraPrimary)
                            .frame(width: 10, height: 10)
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(selected ? Color.cyraPrimaryContainer.opacity(0.35) : Color.cyraSurface),
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(selected ? Color.cyraPrimary : Color.cyraOutline, lineWidth: 1),
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    VStack(spacing: 10) {
        CyraRadioOptionRow(systemImage: "heart", label: "Single", selected: false, onClick: {})
        CyraRadioOptionRow(systemImage: "lock", label: "Prefer not to say", selected: true, onClick: {})
    }
    .padding(24)
    .cyraThemed()
}
