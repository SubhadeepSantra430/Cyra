package subha.app.cyra.feature.auth.domain

/**
 * Maps a thrown Firebase Auth exception to a displayable string-resource key. Matches on
 * `throwable::class.simpleName` rather than importing GitLive's `FirebaseAuthException`
 * hierarchy directly - keeps this mapper (and therefore `commonMain`) free of the
 * `mobileMain`-only Firebase dependency while still covering its exception names
 * (`FirebaseAuthUserCollisionException`, `FirebaseAuthWeakPasswordException`, etc, which
 * all subclass `FirebaseAuthException`/`FirebaseException`).
 */
object AuthErrorMapper {
    fun toMessageKey(throwable: Throwable): String {
        val name = throwable::class.simpleName.orEmpty()
        return when {
            name.contains("Collision") -> "auth_error_email_in_use"
            name.contains("WeakPassword") -> "auth_error_password_weak"
            name.contains("InvalidCredentials") || name.contains("InvalidUser") -> "auth_error_invalid_credentials"
            name.contains("RecentLoginRequired") -> "auth_error_recent_login_required"
            name.contains("Network") -> "auth_error_network"
            else -> "auth_error_generic"
        }
    }
}
