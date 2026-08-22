import SwiftUI

/// The one text field used across Auth (and any future form) - mirrors Android's
/// `CyraTextField.kt`. Rounded-rect outline (16pt corners), leading SF Symbol, optional
/// secure-entry mode with an eye/eye-slash toggle, optional error text below.
struct CyraTextField: View {
    let placeholder: String
    @Binding var text: String
    let systemImage: String
    var isSecure: Bool = false
    var isVisible: Bool = true
    var onToggleVisibility: (() -> Void)? = nil
    var errorText: String? = nil
    var keyboardType: UIKeyboardType = .default
    var disabled: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 10) {
                Image(systemName: systemImage)
                    .foregroundColor(.cyraOnSurfaceVariant)
                    .frame(width: 20)

                Group {
                    if isSecure && !isVisible {
                        SecureField(placeholder, text: $text)
                    } else {
                        TextField(placeholder, text: $text)
                    }
                }
                .font(CyraFont.bodyLarge())
                .keyboardType(keyboardType)
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .disabled(disabled)

                if isSecure {
                    Button(action: { onToggleVisibility?() }) {
                        Image(systemName: isVisible ? "eye.slash" : "eye")
                            .foregroundColor(.cyraOnSurfaceVariant)
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.cyraSurface),
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(errorText != nil ? Color.cyraError : Color.cyraOutline, lineWidth: 1),
            )

            if let errorText {
                Text(errorText)
                    .font(CyraFont.bodySmall())
                    .foregroundColor(.cyraError)
                    .padding(.horizontal, 4)
            }
        }
    }
}

#Preview {
    VStack(spacing: 16) {
        CyraTextField(placeholder: "Email", text: .constant(""), systemImage: "envelope")
        CyraTextField(
            placeholder: "Password",
            text: .constant("hunter2"),
            systemImage: "lock",
            isSecure: true,
            isVisible: false,
            errorText: "Password must be at least 8 characters",
        )
    }
    .padding(24)
    .cyraThemed()
}
