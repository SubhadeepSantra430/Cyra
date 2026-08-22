import SwiftUI

/// The outlined "Continue with X" row style, mirrors Android's `CyraSocialButton.kt`.
/// Used as `Button { HStack { icon; Text(title) } }.buttonStyle(CyraSocialButtonStyle())`.
struct CyraSocialButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(CyraFont.labelLarge())
            .foregroundColor(.cyraOnSurface)
            .frame(maxWidth: .infinity, minHeight: 52)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.cyraSurface.opacity(configuration.isPressed ? 0.85 : 1)),
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(Color.cyraOutline, lineWidth: 1),
            )
    }
}

#Preview {
    VStack(spacing: 12) {
        Button(action: {}) {
            HStack(spacing: 10) {
                Image("GoogleLogo").resizable().frame(width: 20, height: 20)
                Text("Continue with Google")
            }
        }
        .buttonStyle(CyraSocialButtonStyle())

        Button(action: {}) {
            HStack(spacing: 10) {
                Image(systemName: "apple.logo").frame(width: 20, height: 20)
                Text("Continue with Apple")
            }
        }
        .buttonStyle(CyraSocialButtonStyle())
    }
    .padding(24)
    .cyraThemed()
}
