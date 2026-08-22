import SwiftUI

/// Mirrors Android's `CyraSnackbarType`/`CyraSnackbarMessage`/`CyraSnackbarController`.
enum CyraSnackbarType: Equatable {
    case success
    case error
}

struct CyraSnackbarMessage: Identifiable, Equatable {
    let id: Int
    let text: String
    let type: CyraSnackbarType
}

/// Owns the single "current message" queue-of-one for the whole app - any view can call
/// `showSuccess`/`showError` (via `@EnvironmentObject`) and it surfaces through whichever
/// view has `.cyraSnackbarHost()` applied (one, at the app root; see `iOSApp.swift`).
/// This is the piece that decouples "something happened" from "how it's drawn" - views
/// never construct a `CyraSnackbar` themselves.
@MainActor
final class CyraSnackbarController: ObservableObject {
    @Published private(set) var currentMessage: CyraSnackbarMessage?

    private var nextId = 0
    private var dismissTask: Task<Void, Never>?

    func showSuccess(_ text: String) { show(text, type: .success) }
    func showError(_ text: String) { show(text, type: .error) }

    private func show(_ text: String, type: CyraSnackbarType) {
        let message = CyraSnackbarMessage(id: nextId, text: text, type: type)
        nextId += 1
        currentMessage = message

        dismissTask?.cancel()
        dismissTask = Task { [weak self] in
            try? await Task.sleep(nanoseconds: 3_500_000_000)
            guard !Task.isCancelled else { return }
            self?.dismiss(id: message.id)
        }
    }

    /// No-ops if `id` is no longer the message showing (a newer one already replaced it).
    func dismiss(id: Int) {
        if currentMessage?.id == id { currentMessage = nil }
    }
}

/**
 * The reusable, decoupled snackbar itself - a pure function of (text, type). Green
 * background + checkmark for success, red background + xmark for error; same font/
 * corner-radius/spacing language as the rest of the app (`CyraTextField`,
 * `CyraPrimaryButtonStyle`). Knows nothing about the controller or auto-dismiss timing,
 * so it can be dropped into a preview, a different host, or a future non-Auth feature
 * with zero changes.
 */
struct CyraSnackbar: View {
    let text: String
    let type: CyraSnackbarType

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: type == .success ? "checkmark" : "xmark")
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(.white)
            Text(text)
                .font(CyraFont.bodyMedium())
                .foregroundColor(.white)
            Spacer(minLength: 0)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(type == .success ? Color.cyraSuccess : Color.cyraError),
        )
    }
}

private struct CyraSnackbarHostModifier: ViewModifier {
    @EnvironmentObject private var snackbarController: CyraSnackbarController

    func body(content: Content) -> some View {
        // Top-aligned, just below the status bar/notch - overlay respects the safe area
        // by default (no .ignoresSafeArea() here), so no extra top padding is needed
        // beyond a little breathing room.
        content.overlay(alignment: .top) {
            if let message = snackbarController.currentMessage {
                CyraSnackbar(text: message.text, type: message.type)
                    .padding(.horizontal, 16)
                    .padding(.top, 16)
                    .transition(.move(edge: .top).combined(with: .opacity))
            }
        }
        .animation(.easeInOut(duration: 0.2), value: snackbarController.currentMessage)
    }
}

extension View {
    /// Mounted once at the app root (`CyraRootView` in `iOSApp.swift`) - overlays the
    /// current global snackbar message, if any, on top of whatever screen is showing.
    func cyraSnackbarHost() -> some View {
        modifier(CyraSnackbarHostModifier())
    }
}

#Preview {
    VStack(spacing: 16) {
        CyraSnackbar(text: "Account created successfully!", type: .success)
        CyraSnackbar(text: "Incorrect email or password", type: .error)
    }
    .padding(24)
    .cyraThemed()
}
