import SwiftUI

struct ProfileSetupNameStep: View {
    let name: String
    let nameError: String?
    let onNameChange: (String) -> Void

    var body: some View {
        ProfileSetupStepScaffold(
            title: String(localized: "profile_setup_name_title"),
            subtitle: String(localized: "profile_setup_name_subtitle"),
        ) {
            Text(String(localized: "profile_setup_name_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            CyraTextField(
                placeholder: String(localized: "profile_setup_name_placeholder"),
                text: Binding(get: { name }, set: onNameChange),
                systemImage: "person",
                errorText: nameError.map { String(localized: String.LocalizationValue($0)) },
            )
        }
    }
}
