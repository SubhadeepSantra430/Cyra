import SwiftUI
import SharedLogic

struct ProfileSetupBirthdayStep: View {
    let dateOfBirth: LocalDate?
    let dateOfBirthError: String?
    let onDateOfBirthChange: (LocalDate) -> Void

    var body: some View {
        ProfileSetupStepScaffold(
            title: String(localized: "profile_setup_birthday_title"),
            subtitle: String(localized: "profile_setup_birthday_subtitle"),
        ) {
            Text(String(localized: "profile_setup_birthday_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            CyraDateField(
                date: dateOfBirth,
                placeholder: String(localized: "profile_setup_birthday_placeholder"),
                onDateSelected: onDateOfBirthChange,
                errorText: dateOfBirthError.map { String(localized: String.LocalizationValue($0)) },
            )
        }
    }
}
