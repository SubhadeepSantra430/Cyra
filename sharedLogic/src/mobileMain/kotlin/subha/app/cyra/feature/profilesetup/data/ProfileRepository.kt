package subha.app.cyra.feature.profilesetup.data

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable
import subha.app.cyra.core.firebase.FirebaseClients
import subha.app.cyra.feature.profilesetup.presentation.ProfileSetupState

/**
 * Plain, fully-typed `@Serializable` shape for the Firestore write - GitLive's
 * `DocumentReference.set(data: T, merge: Boolean)` encodes `T` via kotlinx.serialization
 * (see `dev.gitlive.firebase.internal.encodeAsObject`), so this needs a real serializer,
 * not an untyped `Map<String, Any?>`. Dates are stored as plain ISO-8601 strings
 * (`LocalDate.toString()`) rather than native Firestore timestamps - simple and
 * unambiguous, though it means no native Firestore date-range queries against these
 * fields without a follow-up migration if that's ever needed.
 */
@Serializable
private data class ProfileDocument(
    val name: String,
    val dateOfBirth: String,
    val heightCm: Int? = null,
    val weightKg: Int? = null,
    val maritalStatus: String? = null,
    val lastPeriodStartDate: String? = null,
    // Set when the user confirmed the "I don't remember" dialog instead of picking a date.
    val lastPeriodUnknown: Boolean = false,
    val averageCycleLengthDays: Int? = null,
    val averagePeriodDurationDays: Int? = null,
    // Not optional/nullable like the other fields above - "Not sure" is a real, always-
    // present default answer (see ProfileSetupState.cycleRegularity), so this is always
    // written, even if the user never touched that step at all.
    val cycleRegularity: String,
    // A simple completion flag rather than a timestamp - kotlinx.datetime's `Clock` is a
    // deprecated alias for `kotlin.time.Clock` (still `@ExperimentalTime`) as of the
    // version pinned here, and this flag is all a future "skip setup on next login"
    // check would need anyway.
    val profileSetupCompleted: Boolean = true,
)

/**
 * The one place profile-setup data gets written - a single merge-write to
 * `users/{userId}` once the flow completes (`ProfileSetupStep.AllSet`'s "Start My
 * Journey"), not a write per step. Optional fields the user skipped entirely are left
 * out of the document rather than written as an untouched slider default - see
 * [ProfileSetupState.heightProvided]/[ProfileSetupState.weightProvided] and the
 * blank-string-means-unset convention on the two cycle-info text fields.
 * `merge = true` so this never clobbers fields a future edit-profile screen writes
 * independently.
 */
class ProfileRepository(private val firestore: FirebaseFirestore = FirebaseClients.firestore) {

    suspend fun saveProfile(userId: String, state: ProfileSetupState): Result<Unit> = runCatching {
        val dateOfBirth = requireNotNull(state.dateOfBirth) {
            "dateOfBirth is mandatory - ProfileSetupViewModel must not reach AllSet without it"
        }
        val document = ProfileDocument(
            name = state.name.trim(),
            dateOfBirth = dateOfBirth.toString(),
            heightCm = state.heightCm.takeIf { state.heightProvided },
            weightKg = state.weightKg.takeIf { state.weightProvided },
            maritalStatus = state.maritalStatus?.name,
            lastPeriodStartDate = state.lastPeriodStartDate?.toString(),
            lastPeriodUnknown = state.lastPeriodUnknown,
            averageCycleLengthDays = state.averageCycleLengthDays.trim().toIntOrNull(),
            averagePeriodDurationDays = state.averagePeriodDurationDays.trim().toIntOrNull(),
            cycleRegularity = state.cycleRegularity.name,
        )
        firestore.collection("users").document(userId).set(document, merge = true)
    }
}
