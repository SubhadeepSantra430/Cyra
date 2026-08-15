package subha.app.cyra.core.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.storage

/**
 * Thin named accessors over GitLive's `Firebase.auth`/`.firestore`/`.storage` so feature
 * repositories depend on this instead of importing `dev.gitlive.firebase.*` everywhere -
 * keeps the third-party SDK import surface in one place per architecture plan.
 *
 * GitLive auto-initializes the underlying native Firebase app on both platforms from the
 * platform config files (`google-services.json` on Android, `GoogleService-Info.plist`
 * on iOS) - neither of which exist in this repo yet. Add those before wiring the first
 * real Firebase call (Auth feature).
 */
object FirebaseClients {
    val auth: FirebaseAuth get() = Firebase.auth
    val firestore: FirebaseFirestore get() = Firebase.firestore
    val storage: FirebaseStorage get() = Firebase.storage

    /** Current user's ID token, for bearer-authenticating Ktor calls to Cloud Functions. */
    suspend fun currentIdToken(forceRefresh: Boolean = false): String? =
        auth.currentUser?.getIdToken(forceRefresh)
}
