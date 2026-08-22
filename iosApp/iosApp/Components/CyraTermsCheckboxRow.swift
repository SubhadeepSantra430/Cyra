import SwiftUI

/// Checkbox + "I agree to the **Terms of Service** and **Privacy Policy**" - mirrors
/// Android's `CyraTermsCheckboxRow.kt`. Built from separate `Text` segments with their
/// own `.onTapGesture` rather than iOS 15's `AttributedString` link API, since the app's
/// deployment target is iOS 13 (see `Podfile`) - the one limitation is the sentence
/// doesn't reflow as a single wrapped block the way Android's `LinkAnnotation` text
/// does, acceptable for this short, single-line row.
struct CyraTermsCheckboxRow: View {
    let checked: Bool
    let onCheckedChange: (Bool) -> Void
    let onTermsClick: () -> Void
    let onPrivacyClick: () -> Void

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            CyraCheckbox(checked: checked, onCheckedChange: onCheckedChange)
                .padding(.top, 2)

            // `Text` concatenation (`+`) wraps as one block, unlike a `HStack` of `Text`s -
            // the two link spans just aren't independently tappable this way, so instead
            // we accept a non-wrapping single line here (fine for this short sentence).
            HStack(spacing: 0) {
                Text(String(localized: "auth_terms_prefix"))
                    .foregroundColor(.cyraOnSurfaceVariant)
                Text(String(localized: "auth_terms_link"))
                    .foregroundColor(.cyraPrimary)
                    .fontWeight(.semibold)
                    .onTapGesture(perform: onTermsClick)
                Text(String(localized: "auth_terms_middle"))
                    .foregroundColor(.cyraOnSurfaceVariant)
                Text(String(localized: "auth_privacy_link"))
                    .foregroundColor(.cyraPrimary)
                    .fontWeight(.semibold)
                    .onTapGesture(perform: onPrivacyClick)
            }
            .font(CyraFont.bodyMedium())
        }
    }
}

#Preview {
    CyraTermsCheckboxRow(checked: false, onCheckedChange: { _ in }, onTermsClick: {}, onPrivacyClick: {})
        .padding(24)
        .cyraThemed()
}
