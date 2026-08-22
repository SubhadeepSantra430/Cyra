import SwiftUI
import SharedLogic

/// The minimal screen reached from Login's "Forgot password?" link - mirrors Android's
/// `ForgotPasswordScreen.kt`. Not in the reference design, kept deliberately small.
struct ForgotPasswordView: View {
    let onNavigate: (NavigationEvent) -> Void

    @State private var viewModel: ForgotPasswordViewModel
    @State private var state: ForgotPasswordState

    init(onNavigate: @escaping (NavigationEvent) -> Void) {
        self.onNavigate = onNavigate
        let vm = provideForgotPasswordViewModel()
        _viewModel = State(initialValue: vm)
        _state = State(initialValue: vm.uiState.value)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                Spacer().frame(height: 16)
                CyraBackButton(action: viewModel.onBackClicked)

                Spacer().frame(height: 24)
                Text(String(localized: "auth_forgot_password_title"))
                    .font(CyraFont.headlineMedium())
                    .foregroundColor(.cyraOnSurface)
                Spacer().frame(height: 8)
                Text(String(localized: "auth_forgot_password_subtitle"))
                    .font(CyraFont.bodyMedium())
                    .foregroundColor(.cyraOnSurfaceVariant)

                Spacer().frame(height: 24)

                if state.emailSent {
                    Text(String(localized: "auth_forgot_password_sent_title"))
                        .font(CyraFont.titleMedium())
                        .foregroundColor(.cyraOnSurface)
                    Spacer().frame(height: 8)
                    Text(String(localized: "auth_forgot_password_sent_message"))
                        .font(CyraFont.bodyMedium())
                        .foregroundColor(.cyraOnSurfaceVariant)
                    Spacer().frame(height: 24)
                    Button(String(localized: "auth_back_to_login_button"), action: viewModel.onBackClicked)
                        .buttonStyle(CyraSkipButtonStyle())
                } else {
                    CyraTextField(
                        placeholder: String(localized: "auth_email_placeholder"),
                        text: Binding(get: { state.email }, set: viewModel.onEmailChanged),
                        systemImage: "envelope",
                        errorText: state.emailError.map { String(localized: String.LocalizationValue($0)) },
                        keyboardType: .emailAddress,
                        disabled: state.isSubmitting,
                    )
                    Spacer().frame(height: 24)
                    Button(String(localized: "auth_send_reset_link_button"), action: viewModel.onSubmitClicked)
                        .buttonStyle(CyraPrimaryButtonStyle(isEnabled: !state.isSubmitting))
                        .frame(maxWidth: .infinity)
                        .disabled(state.isSubmitting)
                }
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
                if let navigate = effect as? AuthEffectNavigate {
                    onNavigate(navigate.event)
                }
                // ShowError isn't surfaced here - validation already renders through
                // state.emailError, and other failures are rare for this screen.
            }
        }
    }
}
