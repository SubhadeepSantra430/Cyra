import SwiftUI
import SharedLogic

/// This step is mandatory - see `ProfileSetupState.isPrimaryButtonEnabled`. Uses
/// SwiftUI's native `.alert` (not a custom-styled popup like Android's
/// `CyraAlertDialog`) for the "I don't remember" reassurance dialog, per platform
/// convention - iOS system alerts aren't meant to be restyled.
struct ProfileSetupLastPeriodStep: View {
    let lastPeriodStartDate: LocalDate?
    let onDateChange: (LocalDate) -> Void
    let onDontRememberConfirmed: () -> Void

    @State private var showDontRememberAlert = false

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
            // The mandatory requirement's escape hatch - confirming the alert satisfies
            // it without an exact date (see ProfileSetupViewModel.onLastPeriodUnknownConfirmed).
            Button(String(localized: "profile_setup_last_period_dont_remember"), action: { showDontRememberAlert = true })
                .buttonStyle(CyraSkipButtonStyle())
        }
        .alert(
            String(localized: "profile_setup_last_period_dont_remember_title"),
            isPresented: $showDontRememberAlert,
        ) {
            Button(String(localized: "profile_setup_last_period_dont_remember_cancel"), role: .cancel) {}
            Button(String(localized: "profile_setup_last_period_dont_remember_confirm"), action: onDontRememberConfirmed)
        } message: {
            Text(String(localized: "profile_setup_last_period_dont_remember_message"))
        }
    }
}
