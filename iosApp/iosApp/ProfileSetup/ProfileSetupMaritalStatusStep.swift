import SwiftUI
import SharedLogic

struct ProfileSetupMaritalStatusStep: View {
    let selected: MaritalStatus?
    let onSelected: (MaritalStatus) -> Void

    private static let allStatuses: [MaritalStatus] = [.single, .married, .divorced, .widowed, .preferNotToSay]

    var body: some View {
        ProfileSetupStepScaffold(
            title: String(localized: "profile_setup_marital_status_title"),
            subtitle: String(localized: "profile_setup_marital_status_subtitle"),
        ) {
            Text(String(localized: "profile_setup_marital_status_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)
            VStack(spacing: 10) {
                ForEach(Array(Self.allStatuses.enumerated()), id: \.offset) { _, status in
                    CyraRadioOptionRow(
                        systemImage: systemImage(for: status),
                        label: String(localized: String.LocalizationValue(status.messageKey)),
                        selected: selected == status,
                        onClick: { onSelected(status) },
                    )
                }
            }
        }
    }

    private func systemImage(for status: MaritalStatus) -> String {
        switch status {
        case .single: return "heart"
        case .married: return "person.2.fill"
        case .divorced: return "heart.slash"
        case .widowed: return "person.fill"
        case .preferNotToSay: return "lock"
        default: return "questionmark.circle"
        }
    }
}
