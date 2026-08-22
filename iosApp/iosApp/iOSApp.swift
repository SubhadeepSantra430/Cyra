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

/// Handles the handoff from CyraSplashView to the real app flow - `AppStartupViewModel`
/// resolves once, offline-safe, which of onboarding/auth/profile-setup-resume/home a
/// cold start should land on (see that class and `AppStartupDestination` on the Kotlin
/// side). The brand splash stays up until *both* its own minimum duration AND that
/// resolution have completed, whichever is later - mirrors Android's `CyraRoot`.
struct CyraRootView: View {
    @State private var minimumSplashDurationElapsed = false
    @State private var startupViewModel = provideAppStartupViewModel()
    @State private var destination: AppStartupDestination?
    // In-session state for the transitions that happen *after* the initial check
    // (onboarding finishes, auth completes, profile setup finishes) - the startup
    // check itself only needs to run once per launch.
    @State private var override: AppStartupDestination?
    // Owns the app-wide snackbar queue - provided here, at the root, so any screen
    // further down can show a global success/error message (see CyraSnackbar.swift)
    // without threading a controller reference through every navigation call.
    @StateObject private var snackbarController = CyraSnackbarController()

    private var showSplash: Bool { !minimumSplashDurationElapsed || destination == nil }
    private var resolvedDestination: AppStartupDestination? { override ?? destination }

    var body: some View {
        ZStack {
            if showSplash {
                CyraSplashView()
                    .transition(.opacity)
            } else if let destination = resolvedDestination {
                destinationView(for: destination)
                    .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: showSplash)
        .task {
            try? await Task.sleep(nanoseconds: splashDurationSeconds)
            minimumSplashDurationElapsed = true
        }
        .task {
            for await newDestination in startupViewModel.destination {
                destination = newDestination
            }
        }
        .cyraThemed()
        .cyraSnackbarHost(snackbarController)
    }

    @ViewBuilder
    private func destinationView(for destination: AppStartupDestination) -> some View {
        switch destination {
        case is AppStartupDestinationNeedsOnboardingCarousel:
            OnboardingView(onFinished: {
                startupViewModel.markOnboardingCarouselSeen()
                override = AppStartupDestinationNeedsAuth.shared
            })
        case let needsProfileSetup as AppStartupDestinationNeedsProfileSetup:
            ProfileSetupView(
                userId: needsProfileSetup.userId,
                snackbarController: snackbarController,
                onNavigate: { event in
                    if event is NavigationEventNavigateToHome {
                        override = AppStartupDestinationHome.shared
                    }
                },
            )
        case is AppStartupDestinationNeedsAuth:
            AuthFlowView(
                snackbarController: snackbarController,
                onAuthenticated: { override = AppStartupDestinationHome.shared },
                onExitAuth: { override = AppStartupDestinationNeedsOnboardingCarousel.shared },
                onNeedsProfileSetup: { userId in override = AppStartupDestinationNeedsProfileSetup(userId: userId) },
            )
        default: // AppStartupDestinationHome
            ContentView()
        }
    }
}
