import SwiftUI
import SharedLogic

/// Signup's live password checklist - mirrors Android's `PasswordRequirementsChecklist`.
/// One row per `PasswordRequirementStatus`, updating on every keystroke (the shared
/// `SignupViewModel.onPasswordChanged` recomputes the whole list). Deliberately only used
/// under `SignupView`'s password field: confirm-password has its own separate "do the two
/// match" check, and `LoginView`'s password field has no strength requirement to show.
struct PasswordRequirementsChecklist: View {
    let requirements: [PasswordRequirementStatus]

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            ForEach(Array(requirements.enumerated()), id: \.offset) { _, requirement in
                PasswordRequirementRow(
                    satisfied: requirement.satisfied,
                    text: String(localized: String.LocalizationValue(requirement.requirement.messageKey)),
                )
            }
        }
        .padding(.leading, 4)
        .padding(.top, 8)
    }
}

private struct PasswordRequirementRow: View {
    let satisfied: Bool
    let text: String

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: satisfied ? "checkmark" : "xmark")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(satisfied ? .cyraSuccess : .cyraError)
            Text(text)
                .font(CyraFont.bodySmall())
                .foregroundColor(satisfied ? .cyraSuccess : .cyraError)
        }
    }
}

#Preview {
    PasswordRequirementsChecklist(
        requirements: AuthValidators.shared.passwordRequirementStatuses(value: "Abc123"),
    )
    .padding(24)
    .cyraThemed()
}
