package subha.app.cyra.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/** Placeholder value in `strings.xml` until Google Sign-In is enabled in Firebase console. */
const val GOOGLE_WEB_CLIENT_ID_PLACEHOLDER = "REPLACE_WITH_FIREBASE_WEB_CLIENT_ID"

/**
 * Android's half of Google Sign-In - Credential Manager only yields an ID token (no
 * OAuth access token), which is enough for `GoogleAuthProvider.credential(idToken, null)`
 * on the shared [subha.app.cyra.feature.auth.data.AuthRepository] side.
 *
 * Throws if the user cancels or Credential Manager has no Google account to offer -
 * callers must catch and route to `onGoogleSignInFailed()`, never let this crash the
 * screen.
 */
suspend fun signInWithGoogleCredentialManager(context: Context, webClientId: String): String {
    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(option)
        .build()

    val response = CredentialManager.create(context).getCredential(context, request)
    val credential = response.credential
    require(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        "Unexpected credential type from Credential Manager"
    }
    return GoogleIdTokenCredential.createFrom(credential.data).idToken
}
