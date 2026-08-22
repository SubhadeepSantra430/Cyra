package subha.app.cyra.feature.auth.data

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.OAuthProvider
import subha.app.cyra.core.firebase.FirebaseClients

/**
 * The one place Auth-related Firebase calls happen - wraps [FirebaseClients.auth]
 * (GitLive's KMP `FirebaseAuth`) so ViewModels never import `dev.gitlive.firebase.*`
 * directly. Every call surfaces failures via [Result] instead of throwing, so
 * ViewModels can map them through `AuthErrorMapper` uniformly.
 */
class AuthRepository(private val auth: FirebaseAuth = FirebaseClients.auth) {

    val currentUserId: String? get() = auth.currentUser?.uid

    suspend fun login(email: String, password: String): Result<Unit> =
        runCatching { auth.signInWithEmailAndPassword(email, password) }.map {}

    /**
     * `true` result is whether this created a brand-new account - always `true` here in
     * practice (`createUserWithEmailAndPassword` fails with a collision error instead of
     * succeeding against an existing email), but returning it keeps this signature
     * uniform with [signInWithGoogle]/[signInWithApple] below, where it's NOT always
     * true and genuinely needs checking. Callers use it to decide whether to route into
     * profile-setup ("NavigateToOnboarding") or straight to "NavigateToHome".
     */
    suspend fun signup(email: String, password: String): Result<Boolean> =
        runCatching { auth.createUserWithEmailAndPassword(email, password) }
            .map { it.additionalUserInfo?.isNewUser == true }

    suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching { auth.sendPasswordResetEmail(email) }

    /**
     * [accessToken] is optional - Android's Credential Manager flow only yields an ID
     * token. `true` result: whether Firebase created a brand-new account for this
     * credential (first-ever Google sign-in) rather than matching an existing one -
     * Google/Apple sign-in can be tapped from either Login or Signup, so unlike
     * [signup], this genuinely can go either way and callers must check it.
     */
    suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Boolean> =
        runCatching {
            auth.signInWithCredential(GoogleAuthProvider.credential(idToken = idToken, accessToken = accessToken))
        }.map { it.additionalUserInfo?.isNewUser == true }

    /** [rawNonce] must be the raw (unhashed) nonce - Firebase re-hashes it to verify against Apple's response. See [signInWithGoogle] for what the `true` result means. */
    suspend fun signInWithApple(idToken: String, rawNonce: String): Result<Boolean> =
        runCatching {
            auth.signInWithCredential(
                OAuthProvider.credential(providerId = "apple.com", idToken = idToken, rawNonce = rawNonce),
            )
        }.map { it.additionalUserInfo?.isNewUser == true }
}
