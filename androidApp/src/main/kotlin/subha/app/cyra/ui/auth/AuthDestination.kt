package subha.app.cyra.ui.auth

/** The 3 screens the Auth flow swaps between - kept local/state-hoisted, matching the
 * rest of the app (no `NavController`/`NavHost` is wired up anywhere yet). */
sealed interface AuthDestination {
    data object Login : AuthDestination
    data object Signup : AuthDestination
    data object ForgotPassword : AuthDestination
}
