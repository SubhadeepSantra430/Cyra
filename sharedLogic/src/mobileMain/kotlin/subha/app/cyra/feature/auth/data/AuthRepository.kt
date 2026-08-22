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

    suspend fun signup(email: String, password: String): Result<Unit> =
        runCatching { auth.createUserWithEmailAndPassword(email, password) }.map {}

    suspend fun sendPasswordReset(email: String): Result<Unit> =
        runCatching { auth.sendPasswordResetEmail(email) }

    /** [accessToken] is optional - Android's Credential Manager flow only yields an ID token. */
    suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Unit> =
        runCatching {
            auth.signInWithCredential(GoogleAuthProvider.credential(idToken = idToken, accessToken = accessToken))
        }.map {}

    /** [rawNonce] must be the raw (unhashed) nonce - Firebase re-hashes it to verify against Apple's response. */
    suspend fun signInWithApple(idToken: String, rawNonce: String): Result<Unit> =
        runCatching {
            auth.signInWithCredential(
                OAuthProvider.credential(providerId = "apple.com", idToken = idToken, rawNonce = rawNonce),
            )
        }.map {}
}
