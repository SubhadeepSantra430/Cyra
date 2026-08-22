import SharedLogic

/// The one place `AuthEffect` gets interpreted, called from `LoginView`, `SignupView`
/// and `ForgotPasswordView`'s own `.task { for await effect in viewModel.sideEffect }`
/// loop - mirrors Android's `HandleAuthEffects`. `ShowError`/`ShowSuccess` both route
/// into the single global `CyraSnackbarController` instead of each view keeping its own
/// `errorMessageKey` state rendered as an inline `Text` banner.
@MainActor
func handleAuthEffect(
    _ effect: AuthEffect,
    onNavigate: (NavigationEvent) -> Void,
    snackbarController: CyraSnackbarController,
) {
    if let navigate = effect as? AuthEffectNavigate {
        onNavigate(navigate.event)
    } else if let showError = effect as? AuthEffectShowError {
        snackbarController.showError(String(localized: String.LocalizationValue(showError.messageKey)))
    } else if let showSuccess = effect as? AuthEffectShowSuccess {
        snackbarController.showSuccess(String(localized: String.LocalizationValue(showSuccess.messageKey)))
    }
}
