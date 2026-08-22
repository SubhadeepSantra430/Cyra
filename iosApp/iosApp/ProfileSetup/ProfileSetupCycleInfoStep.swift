import SwiftUI
import SharedLogic

struct ProfileSetupCycleInfoStep: View {
    let averageCycleLengthDays: String
    let averagePeriodDurationDays: String
    let cycleRegularity: CycleRegularity?
    let onCycleLengthChange: (String) -> Void
    let onPeriodDurationChange: (String) -> Void
    let onCycleRegularityChange: (CycleRegularity) -> Void

    private static let allRegularities: [CycleRegularity] = [.regular, .irregular, .notSure]

    var body: some View {
        ProfileSetupStepScaffold(
            title: String(localized: "profile_setup_cycle_info_title"),
            subtitle: String(localized: "profile_setup_cycle_info_subtitle"),
        ) {
            Text(String(localized: "profile_setup_cycle_length_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            suffixField(value: averageCycleLengthDays, onChange: onCycleLengthChange, placeholder: "28")

            Spacer().frame(height: 20)
            Text(String(localized: "profile_setup_period_duration_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            suffixField(value: averagePeriodDurationDays, onChange: onPeriodDurationChange, placeholder: "5")

            Spacer().frame(height: 20)
            Text(String(localized: "profile_setup_cycle_regularity_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            CyraSegmentedToggle(
                options: Self.allRegularities.map { String(localized: String.LocalizationValue($0.messageKey)) },
                selectedIndex: cycleRegularity.flatMap { r in Self.allRegularities.firstIndex(of: r) } ?? -1,
                onOptionSelected: { index in onCycleRegularityChange(Self.allRegularities[index]) },
            )
        }
    }

    /// A plain outlined field with a trailing "days" label, no leading icon - mirrors
    /// Android's `CyraTextField(leadingIcon = null, trailingLabel = "days")`. iOS's
    /// `CyraTextField` always shows a leading `systemImage`, so this is built directly
    /// rather than extending that component for one field shape used nowhere else yet.
    private func suffixField(value: String, onChange: @escaping (String) -> Void, placeholder: String) -> some View {
        HStack {
            TextField(placeholder, text: Binding(get: { value }, set: onChange))
                .font(CyraFont.bodyLarge())
                .keyboardType(.numberPad)
            Text(String(localized: "profile_setup_days_suffix"))
                .font(CyraFont.bodyMedium())
                .foregroundColor(.cyraOnSurfaceVariant)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(RoundedRectangle(cornerRadius: 16).fill(Color.cyraSurface))
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.cyraOutline, lineWidth: 1))
    }
}
