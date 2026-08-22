import SwiftUI

/// The completion screen - no category badge, no form fields. "ProfileSetupAllSetIllustration"
/// is cropped directly from the reference design (a shield + florals, not an SF Symbol
/// like the rest of this screen's icons - matches the reference's original raster asset
/// instead of approximating it, which read as noticeably flatter than the source).
struct ProfileSetupAllSetStep: View {
    var body: some View {
        VStack {
            Text(String(localized: "profile_setup_all_set_title"))
                .font(CyraFont.headlineMedium())
                .foregroundColor(.cyraOnSurface)
                .multilineTextAlignment(.center)
            Spacer().frame(height: 8)
            Text(String(localized: "profile_setup_all_set_subtitle"))
                .font(CyraFont.bodyMedium())
                .foregroundColor(.cyraOnSurfaceVariant)
                .multilineTextAlignment(.center)
            Spacer().frame(height: 24)
            Image("ProfileSetupAllSetIllustration")
                .resizable()
                .scaledToFit()
                .frame(maxWidth: .infinity)
            Spacer().frame(height: 24)
            HStack(spacing: 12) {
                Image(systemName: "lock")
                    .foregroundColor(.cyraOnSurfaceVariant)
                VStack(alignment: .leading, spacing: 2) {
                    Text(String(localized: "profile_setup_all_set_privacy_title"))
                        .font(CyraFont.titleSmall())
                        .foregroundColor(.cyraOnSurface)
                    Text(String(localized: "profile_setup_all_set_privacy_subtitle"))
                        .font(CyraFont.bodySmall())
                        .foregroundColor(.cyraOnSurfaceVariant)
                }
                Spacer()
            }
            .padding(16)
            .background(RoundedRectangle(cornerRadius: 16).fill(Color.cyraPrimaryContainer.opacity(0.4)))
        }
    }
}

#Preview {
    ProfileSetupAllSetStep()
        .padding(24)
        .cyraThemed()
}
