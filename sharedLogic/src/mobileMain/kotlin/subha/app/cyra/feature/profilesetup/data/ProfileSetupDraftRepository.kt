package subha.app.cyra.feature.profilesetup.data

import kotlinx.datetime.LocalDate
import subha.app.cyra.feature.profilesetup.domain.CycleRegularity
import subha.app.cyra.feature.profilesetup.domain.HeightUnit
import subha.app.cyra.feature.profilesetup.domain.MaritalStatus
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupStep
import subha.app.cyra.feature.profilesetup.domain.WeightUnit
import subha.app.cyra.feature.profilesetup.presentation.ProfileSetupState

/**
 * The offline-first read/write path for [ProfileSetupDraftEntity] - `ProfileSetupViewModel`
 * calls [loadDraft] once at startup to resume, and [saveDraft] on every state change
 * afterward (see that class's debounced `uiState`-observer). [clearDraft] runs only once
 * the flow's single Firestore write (`ProfileRepository.saveProfile`) actually succeeds -
 * never optimistically.
 */
class ProfileSetupDraftRepository(private val dao: ProfileSetupDraftDao) {

    suspend fun saveDraft(userId: String, state: ProfileSetupState) {
        dao.upsert(state.toEntity(userId))
    }

    suspend fun loadDraft(userId: String): ProfileSetupState? =
        dao.getByUserId(userId)?.toState()

    suspend fun clearDraft(userId: String) {
        dao.deleteByUserId(userId)
    }

    private fun ProfileSetupState.toEntity(userId: String): ProfileSetupDraftEntity = ProfileSetupDraftEntity(
        userId = userId,
        step = step.name,
        name = name,
        dateOfBirth = dateOfBirth?.toString(),
        heightCm = heightCm,
        heightProvided = heightProvided,
        heightUnit = heightUnit.name,
        weightKg = weightKg,
        weightProvided = weightProvided,
        weightUnit = weightUnit.name,
        maritalStatus = maritalStatus?.name,
        lastPeriodStartDate = lastPeriodStartDate?.toString(),
        lastPeriodUnknown = lastPeriodUnknown,
        averageCycleLengthDays = averageCycleLengthDays,
        averagePeriodDurationDays = averagePeriodDurationDays,
        cycleRegularity = cycleRegularity.name,
        updatedAtEpochMillis = 0L, // not read anywhere yet - see AppSettings/SyncMetadata's own unused equivalents
    )

    /**
     * [step] is parsed defensively - a future rename/reorder of [ProfileSetupStep]'s
     * entries shouldn't be able to crash a resume for someone with an old draft row on
     * disk; falling back to the flow's own entry point is the safe default.
     */
    private fun ProfileSetupDraftEntity.toState(): ProfileSetupState = ProfileSetupState(
        step = runCatching { ProfileSetupStep.valueOf(step) }.getOrDefault(ProfileSetupStep.Name),
        name = name,
        dateOfBirth = dateOfBirth?.let { LocalDate.parse(it) },
        heightCm = heightCm,
        heightProvided = heightProvided,
        heightUnit = runCatching { HeightUnit.valueOf(heightUnit) }.getOrDefault(HeightUnit.CM),
        weightKg = weightKg,
        weightProvided = weightProvided,
        weightUnit = runCatching { WeightUnit.valueOf(weightUnit) }.getOrDefault(WeightUnit.KG),
        maritalStatus = maritalStatus?.let { runCatching { MaritalStatus.valueOf(it) }.getOrNull() },
        lastPeriodStartDate = lastPeriodStartDate?.let { LocalDate.parse(it) },
        lastPeriodUnknown = lastPeriodUnknown,
        averageCycleLengthDays = averageCycleLengthDays,
        averagePeriodDurationDays = averagePeriodDurationDays,
        cycleRegularity = runCatching { CycleRegularity.valueOf(cycleRegularity) }.getOrDefault(CycleRegularity.NotSure),
        isLoadingDraft = false,
    )
}
