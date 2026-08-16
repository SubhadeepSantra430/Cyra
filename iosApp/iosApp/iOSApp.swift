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

/// Handles the handoff from CyraSplashView to onboarding, then to the (currently
/// placeholder) app content - mirrors `CyraRoot`/`CyraAppFlow` on Android. Once real
/// navigation/auth-state exists (Auth feature), the splash's timed delay is replaced by
/// "stay on splash until the auth-state check completes", and onboarding's
/// `onFinished` will route to real auth/home instead of the placeholder.
struct CyraRootView: View {
    @State private var showSplash = true
    @State private var onboardingComplete = false

    var body: some View {
        ZStack {
            if showSplash {
                CyraSplashView()
                    .transition(.opacity)
            } else if !onboardingComplete {
                OnboardingView(onFinished: { onboardingComplete = true })
                    .transition(.opacity)
            } else {
                // TODO(Auth feature): replace with real navigation once auth/home exists.
                ContentView()
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: showSplash)
        .animation(.easeInOut(duration: 0.3), value: onboardingComplete)
        .task {
            try? await Task.sleep(nanoseconds: splashDurationSeconds)
            showSplash = false
        }
        .cyraThemed()
    }
}
