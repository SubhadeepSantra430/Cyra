package subha.app.cyra.core.presentation

/**
 * Vocabulary shared ViewModels use to ask the native UI layer to navigate, without ever
 * holding a `NavController`/`NavigationStack` reference themselves. Each feature's
 * `SideEffect` type typically wraps or extends these; both platforms translate the
 * events they care about into their own native navigation call:
 *  - Android: a `LaunchedEffect` collecting `viewModel.sideEffect`, calling `navController.navigate(...)`.
 *  - iOS: a `.task { for await effect in viewModel.sideEffect }`, calling into `AppRouter`.
 */
sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
    data object NavigateToHome : NavigationEvent
    data object NavigateToLogin : NavigationEvent
    data object NavigateToSignup : NavigationEvent
    data object NavigateToForgotPassword : NavigationEvent
    data class NavigateToOnboarding(val userId: String) : NavigationEvent
}
