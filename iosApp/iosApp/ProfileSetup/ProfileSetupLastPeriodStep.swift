import SwiftUI
import SharedLogic

struct ProfileSetupLastPeriodStep: View {
    let lastPeriodStartDate: LocalDate?
    let onDateChange: (LocalDate) -> Void
    let onDontRememberClick: () -> Void

    var body: some View {
        ProfileSetupStepScaffold(
            title: String(localized: "profile_setup_last_period_title"),
            subtitle: String(localized: "profile_setup_last_period_subtitle"),
        ) {
            Text(String(localized: "profile_setup_last_period_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            CyraDateField(
                date: lastPeriodStartDate,
                placeholder: String(localized: "profile_setup_last_period_placeholder"),
                onDateSelected: onDateChange,
            )
            Spacer().frame(height: 12)
            // Functionally identical to the bottom "Skip" button (advances without
            // setting a date) - kept as a separate, more specific-sounding affordance
            // right next to the field, matching the reference design.
            Button(String(localized: "profile_setup_last_period_dont_remember"), action: onDontRememberClick)
                .buttonStyle(CyraSkipButtonStyle())
        }
    }
}
