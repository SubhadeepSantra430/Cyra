import SwiftUI

/// A 2-4 option switch - mirrors Android's `CyraSegmentedToggle.kt`. The height/weight
/// unit toggle (cm/ft-in, kg/lb) and the "How is your cycle?" 3-way choice
/// (Regular/Irregular/Not sure) are both this same view, just with a different
/// `options` list. Not a Boolean-only toggle - stores the selected index so a third
/// option (cycle regularity) needs no separate view.
///
/// Each option is its own pill: unselected options show an outline only (no fill), the
/// selected option shows a solid fill (no border) - a first draft had unselected
/// options as plain, borderless text, which didn't read as tappable.
struct CyraSegmentedToggle: View {
    let options: [String]
    let selectedIndex: Int
    let onOptionSelected: (Int) -> Void

    var body: some View {
        HStack(spacing: 8) {
            ForEach(Array(options.enumerated()), id: \.offset) { index, label in
                let selected = index == selectedIndex
                Text(label)
                    .font(CyraFont.labelMedium())
                    .foregroundColor(selected ? .cyraOnPrimary : .cyraOnSurfaceVariant)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(
                        RoundedRectangle(cornerRadius: 10)
                            .fill(selected ? Color.cyraPrimary : Color.clear),
                    )
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(selected ? Color.clear : Color.cyraOutline, lineWidth: 1),
                    )
                    .onTapGesture { onOptionSelected(index) }
            }
        }
    }
}

#Preview {
    CyraSegmentedToggle(options: ["Regular", "Irregular", "Not sure"], selectedIndex: 0, onOptionSelected: { _ in })
        .padding(24)
        .cyraThemed()
}
