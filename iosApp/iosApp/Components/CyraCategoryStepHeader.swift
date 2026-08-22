import SwiftUI

/// The header shared by every step of the profile-setup flow - mirrors Android's
/// `CyraCategoryStepHeader.kt`. An optional `CyraBackButton` on the left, a plain-text
/// category title centered between it and the step counter (no chip/pill background -
/// a first draft used one and it read as a badge rather than a title), and a linear
/// progress bar underneath.
///
/// `category`/`onBackClick` are both optional - the first step in a flow typically
/// hides the back button (nothing to go back to), and a completion screen typically has
/// no category to show.
struct CyraCategoryStepHeader: View {
    let stepNumber: Int
    let totalSteps: Int
    let category: String?
    let onBackClick: (() -> Void)?

    var body: some View {
        VStack(spacing: 14) {
            HStack(alignment: .center) {
                Group {
                    if let onBackClick {
                        CyraBackButton(action: onBackClick)
                    } else {
                        Color.clear.frame(width: 40, height: 40)
                    }
                }
                Text(category ?? "")
                    .font(CyraFont.titleSmall())
                    .foregroundColor(.cyraPrimary)
                    .frame(maxWidth: .infinity)
                    .multilineTextAlignment(.center)
                Text("\(stepNumber) of \(totalSteps)")
                    .font(CyraFont.bodySmall())
                    .foregroundColor(.cyraOnSurfaceVariant)
                    .frame(minWidth: 44, alignment: .trailing)
            }
            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 2)
                        .fill(Color.cyraOutline.opacity(0.4))
                    RoundedRectangle(cornerRadius: 2)
                        .fill(Color.cyraPrimary)
                        .frame(width: geometry.size.width * CGFloat(stepNumber) / CGFloat(totalSteps))
                }
            }
            .frame(height: 4)
        }
    }
}

#Preview {
    CyraCategoryStepHeader(stepNumber: 3, totalSteps: 8, category: "About You", onBackClick: {})
        .padding(24)
        .cyraThemed()
}
