import SwiftUI
import SharedLogic
import GoogleSignIn
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        KoinHelper.shared.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            CyraRootView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
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
    // TODO(Auth feature): replace with a real session check (e.g. FirebaseClients.shared
    // .auth.currentUser != nil) once app-start persistence exists.
    @State private var isAuthenticated = false
    // Non-nil while a brand-new account is running the post-signup profile-setup flow -
    // see AuthFlowView's NavigationEventNavigateToOnboarding handling.
    @State private var profileSetupUserId: String?
    // Owns the app-wide snackbar queue - provided here, at the root, so any screen
    // further down can show a global success/error message (see CyraSnackbar.swift)
    // without threading a controller reference through every navigation call.
    @StateObject private var snackbarController = CyraSnackbarController()

    var body: some View {
        ZStack {
            if showSplash {
                CyraSplashView()
                    .transition(.opacity)
            } else if !onboardingComplete {
                OnboardingView(onFinished: { onboardingComplete = true })
                    .transition(.opacity)
            } else if let userId = profileSetupUserId {
                ProfileSetupView(
                    userId: userId,
                    onNavigate: { event in
                        if event is NavigationEventNavigateToHome {
                            profileSetupUserId = nil
                            isAuthenticated = true
                        }
                    },
                )
                .transition(.opacity)
            } else if !isAuthenticated {
                AuthFlowView(
                    onAuthenticated: { isAuthenticated = true },
                    onExitAuth: { onboardingComplete = false },
                    onNeedsProfileSetup: { userId in profileSetupUserId = userId },
                )
                .transition(.opacity)
            } else {
                ContentView()
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: showSplash)
        .animation(.easeInOut(duration: 0.3), value: onboardingComplete)
        .animation(.easeInOut(duration: 0.3), value: isAuthenticated)
        .task {
            try? await Task.sleep(nanoseconds: splashDurationSeconds)
            showSplash = false
        }
        .cyraThemed()
        .environmentObject(snackbarController)
        .cyraSnackbarHost()
    }
}
