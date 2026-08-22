import SwiftUI
import SharedLogic

/// Owns which Auth screen is showing and translates each screen's `NavigationEvent`s
/// into either a destination change or one of the two flow-level callbacks - mirrors
/// Android's `AuthFlow.kt`.
struct AuthFlowView: View {
    let snackbarController: CyraSnackbarController
    let onAuthenticated: () -> Void
    let onExitAuth: () -> Void
    let onNeedsProfileSetup: (_ userId: String) -> Void

    @State private var destination: AuthDestination = .login

    var body: some View {
        Group {
            switch destination {
            case .login:
                LoginView(snackbarController: snackbarController, onNavigate: handleNavigation)
            case .signup:
                SignupView(snackbarController: snackbarController, onNavigate: handleNavigation)
            case .forgotPassword:
                ForgotPasswordView(snackbarController: snackbarController, onNavigate: handleNavigation)
            }
        }
        .animation(.easeInOut(duration: 0.2), value: destination)
    }

    private func handleNavigation(_ event: NavigationEvent) {
        switch event {
        case is NavigationEventNavigateToSignup:
            destination = .signup
        case is NavigationEventNavigateToForgotPassword:
            destination = .forgotPassword
        case is NavigationEventNavigateToLogin:
            destination = .login
        case is NavigationEventNavigateToHome:
            onAuthenticated()
        case is NavigationEventNavigateBack:
            switch destination {
            // Login is the flow's entry point - there's nothing "behind" it inside Auth
            // yet, so back exits the whole flow (back to onboarding).
            case .login: onExitAuth()
            case .signup, .forgotPassword: destination = .login
            }
        case let onboarding as NavigationEventNavigateToOnboarding:
            // A brand-new account (email signup, or a first-ever Google/Apple sign-in) -
            // exit Auth into the profile-setup flow instead of straight home.
            onNeedsProfileSetup(onboarding.userId)
        default:
            break
        }
    }
}
