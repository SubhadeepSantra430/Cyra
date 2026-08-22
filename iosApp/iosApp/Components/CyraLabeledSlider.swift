import SwiftUI

/// A `Slider` with a floating value bubble that tracks the thumb (e.g. "165 cm") and
/// min/max/intermediate tick labels underneath - mirrors Android's
/// `CyraLabeledSlider.kt`. The bubble's horizontal position is computed from the same
/// fraction the thumb sits at, via `GeometryReader` - close enough for a value hint,
/// not pixel-exact with the thumb's true touch-target center (SwiftUI doesn't expose
/// that either).
struct CyraLabeledSlider: View {
    let value: Int
    let range: ClosedRange<Int>
    let valueLabel: String
    let ticks: [Int]
    let onValueChange: (Int) -> Void

    private var fraction: CGFloat {
        CGFloat(value - range.lowerBound) / CGFloat(range.upperBound - range.lowerBound)
    }

    var body: some View {
        VStack(spacing: 8) {
            GeometryReader { geometry in
                Text(valueLabel)
                    .font(CyraFont.labelMedium())
                    .foregroundColor(.cyraOnPrimary)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 6)
                    .background(Capsule().fill(Color.cyraPrimary))
                    .fixedSize()
                    .position(x: fraction * geometry.size.width, y: geometry.size.height / 2)
            }
            .frame(height: 28)

            Slider(
                value: Binding(
                    get: { Double(value) },
                    set: { onValueChange(Int($0.rounded())) },
                ),
                in: Double(range.lowerBound)...Double(range.upperBound),
                step: 1,
            )
            .tint(.cyraPrimary)

            HStack {
                ForEach(ticks, id: \.self) { tick in
                    Text("\(tick)")
                        .font(CyraFont.bodySmall())
                        .foregroundColor(.cyraOnSurfaceVariant)
                    if tick != ticks.last {
                        Spacer()
                    }
                }
            }
        }
    }
}

#Preview {
    CyraLabeledSlider(value: 165, range: 140...190, valueLabel: "165 cm", ticks: [140, 150, 160, 170, 180, 190], onValueChange: { _ in })
        .padding(24)
        .cyraThemed()
}
