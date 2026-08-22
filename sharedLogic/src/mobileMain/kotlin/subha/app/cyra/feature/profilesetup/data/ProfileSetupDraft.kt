package subha.app.cyra.feature.profilesetup.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

/**
 * The offline-first local mirror of [subha.app.cyra.feature.profilesetup.presentation
 * .ProfileSetupState] - every answer the user has entered so far, plus which step
 * they're on, keyed by [userId] so the flow survives a process death/app restart and
 * resumes exactly where it left off (see [ProfileSetupDraftRepository]).
 *
 * Deliberately NOT built on [subha.app.cyra.core.database.SyncMetadataEntity] - that
 * scaffold tracks per-*feature* sync bookkeeping for data already written and pending
 * push to Firestore (it has no payload columns at all), whereas this is a single
 * accumulating pre-submission draft that gets flushed to Firestore once, at the very
 * end (`ProfileSetupViewModel.submitProfile()`), and cleared locally on success - a
 * different shape entirely.
 *
 * Transient UI-only fields on [ProfileSetupState] (`nameError`, `dateOfBirthError`,
 * `submitAttempted`, `isSubmitting`, `isLoadingDraft`) are NOT persisted here - they're
 * re-derived/reset on every resume, same as they are on every fresh launch of the flow.
 */
@Entity(tableName = "profile_setup_draft")
data class ProfileSetupDraftEntity(
    @PrimaryKey val userId: String,
    val step: String, // ProfileSetupStep.name
    val name: String,
    val dateOfBirth: String?, // ISO yyyy-MM-dd
    val heightCm: Int,
    val heightProvided: Boolean,
    val heightUnit: String,
    val weightKg: Int,
    val weightProvided: Boolean,
    val weightUnit: String,
    val maritalStatus: String?,
    val lastPeriodStartDate: String?,
    val lastPeriodUnknown: Boolean,
    val averageCycleLengthDays: String,
    val averagePeriodDurationDays: String,
    val cycleRegularity: String,
    val updatedAtEpochMillis: Long,
)

@Dao
interface ProfileSetupDraftDao {
    @Upsert
    suspend fun upsert(draft: ProfileSetupDraftEntity)

    @Query("SELECT * FROM profile_setup_draft WHERE userId = :userId")
    suspend fun getByUserId(userId: String): ProfileSetupDraftEntity?

    @Query("DELETE FROM profile_setup_draft WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)
}
