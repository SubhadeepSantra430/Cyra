import SwiftUI

/// A pill-shaped 2-4 option switch - mirrors Android's `CyraSegmentedToggle.kt`. The
/// height/weight unit toggle (cm/ft-in, kg/lb) and the "How is your cycle?" 3-way choice
/// (Regular/Irregular/Not sure) are both this same view, just with a different
/// `options` list. Not a Boolean-only toggle - `selectedIndex` is `-1` for "nothing
/// selected yet" (the cycle-regularity step starts with no default).
struct CyraSegmentedToggle: View {
    let options: [String]
    let selectedIndex: Int
    let onOptionSelected: (Int) -> Void

    var body: some View {
        HStack(spacing: 2) {
            ForEach(Array(options.enumerated()), id: \.offset) { index, label in
                let selected = index == selectedIndex
                Text(label)
                    .font(CyraFont.labelMedium())
                    .foregroundColor(selected ? .cyraOnPrimary : .cyraOnSurfaceVariant)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(selected ? Color.cyraPrimary : Color.clear)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                    .onTapGesture { onOptionSelected(index) }
            }
        }
        .padding(3)
        .background(Color.cyraOutline.opacity(0.25))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

#Preview {
    CyraSegmentedToggle(options: ["Regular", "Irregular", "Not sure"], selectedIndex: 0, onOptionSelected: { _ in })
        .padding(24)
        .cyraThemed()
}
