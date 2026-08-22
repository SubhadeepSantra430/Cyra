import SwiftUI
import SharedLogic

/// Mirrors Android's `SignupScreen.kt`. No name fields, by design - dropped from the
/// reference screenshot for this pass, to be added back in a later module. See
/// `LoginView` for the shared shape (state collection, side-effect handling, both
/// Google and Apple sign-in - iOS gets both, unlike Android's Google-only).
struct SignupView: View {
    let snackbarController: CyraSnackbarController
    let onNavigate: (NavigationEvent) -> Void

    @State private var viewModel: SignupViewModel
    @State private var state: SignupState
    @State private var appleSignInHelper = AppleSignInHelper()

    init(snackbarController: CyraSnackbarController, onNavigate: @escaping (NavigationEvent) -> Void) {
        self.snackbarController = snackbarController
        self.onNavigate = onNavigate
        let vm = provideSignupViewModel()
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
                        Text(String(localized: "auth_signup_title"))
                            .font(CyraFont.headlineMedium())
                            .foregroundColor(.cyraOnSurface)
                        Text(String(localized: "auth_signup_subtitle"))
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
                // Live checklist, Signup's password field only - not confirm-password, not Login.
                if !state.password.isEmpty {
                    PasswordRequirementsChecklist(requirements: state.passwordRequirements)
                }
                Spacer().frame(height: 16)
                CyraTextField(
                    placeholder: String(localized: "auth_confirm_password_placeholder"),
                    text: Binding(get: { state.confirmPassword }, set: viewModel.onConfirmPasswordChanged),
                    systemImage: "lock",
                    isSecure: true,
                    isVisible: state.isConfirmPasswordVisible,
                    onToggleVisibility: viewModel.onToggleConfirmPasswordVisibility,
                    errorText: state.confirmPasswordError.map { String(localized: String.LocalizationValue($0)) },
                    disabled: state.isBusy,
                )

                Spacer().frame(height: 16)
                CyraTermsCheckboxRow(
                    checked: state.agreedToTerms,
                    onCheckedChange: viewModel.onTermsAgreedChanged,
                    onTermsClick: {},
                    onPrivacyClick: {},
                )
                // No inline error here if terms aren't agreed - unlike the fields above,
                // there's no adjacent input box for it to sit under, so SignupViewModel
                // surfaces that failure through the global snackbar instead.

                Spacer().frame(height: 16)
                Button(String(localized: "auth_sign_up_button"), action: viewModel.onSignupClicked)
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
                    Text(String(localized: "auth_has_account_prefix"))
                        .foregroundColor(.cyraOnSurfaceVariant)
                    Text(String(localized: "auth_login_link"))
                        .foregroundColor(.cyraPrimary)
                        .fontWeight(.semibold)
                        .onTapGesture(perform: viewModel.onLoginLinkClicked)
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
