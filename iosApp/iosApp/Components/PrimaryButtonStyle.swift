import SwiftUI

/// The one primary "call to action" button style used everywhere - pill-shaped, filled
/// with the primary color, trailing chevron, matching the onboarding reference design
/// and mirroring Android's `CyraPrimaryButton`. A `ButtonStyle` rather than a fully
/// custom view, per the "use SwiftUI native where it fits" preference - any screen
/// just writes `Button(title) { ... }.buttonStyle(CyraPrimaryButtonStyle())`.
///
/// Deliberately does NOT force `maxWidth: .infinity` here - that would make every use
/// of this style stretch full-width even when it's meant to sit compactly next to a
/// Skip button (mirrors Android's `CyraPrimaryButton`, which is also compact by
/// default). Callers that want a full-width button (e.g. a final "Get Started" CTA)
/// apply `.frame(maxWidth: .infinity)` themselves at the call site.
struct CyraPrimaryButtonStyle: ButtonStyle {
    var isEnabled: Bool = true

    func makeBody(configuration: Configuration) -> some View {
        HStack(spacing: 6) {
            configuration.label
                .font(CyraFont.labelLarge())
            Image(systemName: "chevron.right")
                .font(.system(size: 12, weight: .bold))
        }
        .foregroundColor(.cyraOnPrimary)
        .frame(minHeight: 52)
        .padding(.horizontal, 28)
        .background(
            (isEnabled ? Color.cyraPrimary : Color.cyraPrimary.opacity(0.5))
                .opacity(configuration.isPressed ? 0.85 : 1),
        )
        .clipShape(Capsule())
    }
}

/// The plain-text "skip"/secondary action, matching the reference design (purple text,
/// no background). Mirrors Android's `CyraSkipButton`.
struct CyraSkipButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(CyraFont.labelLarge())
            .foregroundColor(.cyraPrimary)
            .opacity(configuration.isPressed ? 0.6 : 1)
    }
}

#Preview {
    VStack(spacing: 16) {
        Button("Next") {}
            .buttonStyle(CyraPrimaryButtonStyle())
        Button("Skip") {}
            .buttonStyle(CyraSkipButtonStyle())
    }
    .padding(24)
}
