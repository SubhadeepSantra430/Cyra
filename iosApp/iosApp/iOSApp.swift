import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    init() {
        KoinHelper.shared.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            CyraRootView()
        }
    }
}

private let splashDurationSeconds: UInt64 = 1_200_000_000

/// Handles the handoff from CyraSplashView to the real app content - mirrors
/// `CyraRoot`/`CyraApp` on Android. Once real navigation/auth-state exists (Auth
/// feature), this timed delay is replaced by "stay on splash until the auth-state
/// check completes".
struct CyraRootView: View {
    @State private var showSplash = true

    var body: some View {
        ZStack {
            if showSplash {
                CyraSplashView()
                    .transition(.opacity)
            } else {
                ContentView()
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: showSplash)
        .task {
            try? await Task.sleep(nanoseconds: splashDurationSeconds)
            showSplash = false
        }
    }
}
