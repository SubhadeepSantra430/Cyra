import SwiftUI
import SharedLogic

/// Mirrors Android's `LoginScreen.kt`. Backed by the shared `LoginViewModel` - all
/// validation/Firebase calls happen there, this view only renders state and forwards
/// events. Shows BOTH Google and Apple sign-in (Apple is iOS-only, per product
/// decision - Android is Google-only).
struct LoginView: View {
    let onNavigate: (NavigationEvent) -> Void

    @State private var viewModel: LoginViewModel
    @State private var state: LoginState
    @State private var appleSignInHelper = AppleSignInHelper()
    @EnvironmentObject private var snackbarController: CyraSnackbarController

    init(onNavigate: @escaping (NavigationEvent) -> Void) {
        self.onNavigate = onNavigate
        let vm = provideLoginViewModel()
        _viewModel = State(initialValue: vm)
        _state = State(initialValue: vm.uiState.value)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                Spacer().frame(height: 16)
                CyraBackButton(action: viewModel.onBackClicked)

                Spacer().frame(height: 16)
                HStack(alignment: .center) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text(String(localized: "auth_login_title"))
                            .font(CyraFont.headlineMedium())
                            .foregroundColor(.cyraOnSurface)
                        Text(String(localized: "auth_login_subtitle"))
                            .font(CyraFont.bodyMedium())
                            .foregroundColor(.cyraOnSurfaceVariant)
                    }
                    Spacer()
                    Image("AuthIllustration")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 140)
                }

                Spacer().frame(height: 24)

                CyraTextField(
                    placeholder: String(localized: "auth_email_placeholder"),
                    text: Binding(get: { state.email }, set: viewModel.onEmailChanged),
                    systemImage: "envelope",
                    errorText: state.emailError.map { String(localized: String.LocalizationValue($0)) },
                    keyboardType: .emailAddress,
                    disabled: state.isBusy,
                )
                Spacer().frame(height: 16)
                CyraTextField(
                    placeholder: String(localized: "auth_password_placeholder"),
                    text: Binding(get: { state.password }, set: viewModel.onPasswordChanged),
                    systemImage: "lock",
                    isSecure: true,
                    isVisible: state.isPasswordVisible,
                    onToggleVisibility: viewModel.onTogglePasswordVisibility,
                    errorText: state.passwordError.map { String(localized: String.LocalizationValue($0)) },
                    disabled: state.isBusy,
                )

                Spacer().frame(height: 8)
                HStack {
                    Spacer()
                    Button(String(localized: "auth_forgot_password_link"), action: viewModel.onForgotPasswordClicked)
                        .font(CyraFont.bodyMedium())
                        .foregroundColor(.cyraPrimary)
                }

                Spacer().frame(height: 16)
                Button(String(localized: "auth_log_in_button"), action: viewModel.onLoginClicked)
                    .buttonStyle(CyraPrimaryButtonStyle(isEnabled: !state.isBusy))
                    .frame(maxWidth: .infinity)
                    .disabled(state.isBusy)

                Spacer().frame(height: 24)
                CyraDividerWithLabel(text: String(localized: "auth_or_continue_with"))

                Spacer().frame(height: 16)
                Button(action: onGoogleClick) {
                    HStack(spacing: 10) {
                        Image("GoogleLogo").resizable().frame(width: 20, height: 20)
                        Text(String(localized: "auth_continue_with_google"))
                    }
                }
                .buttonStyle(CyraSocialButtonStyle())
                .disabled(state.isBusy)

                Spacer().frame(height: 12)
                Button(action: onAppleClick) {
                    HStack(spacing: 10) {
                        Image(systemName: "apple.logo").frame(width: 20, height: 20)
                        Text(String(localized: "auth_continue_with_apple"))
                    }
                }
                .buttonStyle(CyraSocialButtonStyle())
                .disabled(state.isBusy)

                Spacer().frame(height: 24)
                HStack(spacing: 0) {
                    Spacer()
                    Text(String(localized: "auth_no_account_prefix"))
                        .foregroundColor(.cyraOnSurfaceVariant)
                    Text(String(localized: "auth_signup_link"))
                        .foregroundColor(.cyraPrimary)
                        .fontWeight(.semibold)
                        .onTapGesture(perform: viewModel.onSignupLinkClicked)
                    Spacer()
                }
                .font(CyraFont.bodyMedium())
                Spacer().frame(height: 24)
            }
            .padding(.horizontal, 24)
        }
        .background(Color.cyraBackground.ignoresSafeArea())
        .task {
            for await newState in viewModel.uiState {
                state = newState
            }
        }
        .task {
            for await effect in viewModel.sideEffect {
                handleAuthEffect(effect, onNavigate: onNavigate, snackbarController: snackbarController)
            }
        }
    }

    private func onGoogleClick() {
        guard let viewController = UIApplication.shared.currentViewController else { return }
        Task {
            do {
                let result = try await GoogleSignInHelper.signIn(presenting: viewController)
                viewModel.onGoogleSignInResult(idToken: result.idToken, accessToken: result.accessToken)
            } catch GoogleSignInError.missingClientID {
                // Google Sign-In isn't enabled in Firebase console yet - fail locally
                // instead of routing through the ViewModel's generic failure message.
                snackbarController.showError(String(localized: "auth_error_google_not_configured"))
            } catch {
                viewModel.onGoogleSignInFailed()
            }
        }
    }

    private func onAppleClick() {
        Task {
            do {
                let result = try await appleSignInHelper.signIn()
                viewModel.onAppleSignInResult(idToken: result.idToken, rawNonce: result.rawNonce)
            } catch {
                viewModel.onAppleSignInFailed()
            }
        }
    }
}
