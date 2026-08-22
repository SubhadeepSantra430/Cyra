import SharedLogic

/// Mirrors Auth's `handleAuthEffect` - the one place `ProfileSetupEffect` gets
/// interpreted, called from `ProfileSetupView`'s `.task { for await effect in
/// viewModel.sideEffect }` loop. `ShowError`/`ShowSuccess` both route into the same
/// global `CyraSnackbarController` Auth already uses.
@MainActor
func handleProfileSetupEffect(
    _ effect: ProfileSetupEffect,
    onNavigate: (NavigationEvent) -> Void,
    snackbarController: CyraSnackbarController,
) {
    if let navigate = effect as? ProfileSetupEffectNavigate {
        onNavigate(navigate.event)
    } else if let showError = effect as? ProfileSetupEffectShowError {
        snackbarController.showError(String(localized: String.LocalizationValue(showError.messageKey)))
    } else if let showSuccess = effect as? ProfileSetupEffectShowSuccess {
        snackbarController.showSuccess(String(localized: String.LocalizationValue(showSuccess.messageKey)))
    }
}
