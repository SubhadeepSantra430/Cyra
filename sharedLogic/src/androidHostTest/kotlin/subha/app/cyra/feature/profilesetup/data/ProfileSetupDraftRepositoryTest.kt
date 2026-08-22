package subha.app.cyra.feature.profilesetup.data

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import subha.app.cyra.feature.profilesetup.domain.CycleRegularity
import subha.app.cyra.feature.profilesetup.domain.HeightUnit
import subha.app.cyra.feature.profilesetup.domain.MaritalStatus
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupStep
import subha.app.cyra.feature.profilesetup.domain.WeightUnit
import subha.app.cyra.feature.profilesetup.presentation.ProfileSetupState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** A `Map`-backed fake - no real Room database needed to test the state<->entity mapping. */
private class FakeProfileSetupDraftDao : ProfileSetupDraftDao {
    val rows = mutableMapOf<String, ProfileSetupDraftEntity>()

    override suspend fun upsert(draft: ProfileSetupDraftEntity) {
        rows[draft.userId] = draft
    }

    override suspend fun getByUserId(userId: String): ProfileSetupDraftEntity? = rows[userId]

    override suspend fun deleteByUserId(userId: String) {
        rows.remove(userId)
    }
}

class ProfileSetupDraftRepositoryTest {

    private val dao = FakeProfileSetupDraftDao()
    private val repository = ProfileSetupDraftRepository(dao)

    @Test
    fun saveDraft_thenLoadDraft_roundTripsAllFields() = runBlocking {
        val state = ProfileSetupState(
            step = ProfileSetupStep.CycleInfo,
            name = "Ada",
            dateOfBirth = LocalDate(2000, 7, 23),
            heightCm = 165,
            heightProvided = true,
            heightUnit = HeightUnit.FT_IN,
            weightKg = 60,
            weightProvided = true,
            weightUnit = WeightUnit.LB,
            maritalStatus = MaritalStatus.Single,
            lastPeriodStartDate = LocalDate(2026, 8, 1),
            lastPeriodUnknown = false,
            averageCycleLengthDays = "28",
            averagePeriodDurationDays = "5",
            cycleRegularity = CycleRegularity.Regular,
        )

        repository.saveDraft("user-1", state)
        val loaded = repository.loadDraft("user-1")

        assertEquals(state.step, loaded?.step)
        assertEquals(state.name, loaded?.name)
        assertEquals(state.dateOfBirth, loaded?.dateOfBirth)
        assertEquals(state.heightCm, loaded?.heightCm)
        assertEquals(state.heightProvided, loaded?.heightProvided)
        assertEquals(state.heightUnit, loaded?.heightUnit)
        assertEquals(state.weightKg, loaded?.weightKg)
        assertEquals(state.weightProvided, loaded?.weightProvided)
        assertEquals(state.weightUnit, loaded?.weightUnit)
        assertEquals(state.maritalStatus, loaded?.maritalStatus)
        assertEquals(state.lastPeriodStartDate, loaded?.lastPeriodStartDate)
        assertEquals(state.lastPeriodUnknown, loaded?.lastPeriodUnknown)
        assertEquals(state.averageCycleLengthDays, loaded?.averageCycleLengthDays)
        assertEquals(state.averagePeriodDurationDays, loaded?.averagePeriodDurationDays)
        assertEquals(state.cycleRegularity, loaded?.cycleRegularity)
        assertEquals(false, loaded?.isLoadingDraft) // a loaded draft is never "still loading"
    }

    @Test
    fun loadDraft_returnsNull_whenNoneSaved() = runBlocking {
        assertNull(repository.loadDraft("nobody"))
    }

    @Test
    fun clearDraft_removesIt() = runBlocking {
        repository.saveDraft("user-1", ProfileSetupState())
        repository.clearDraft("user-1")

        assertNull(repository.loadDraft("user-1"))
    }

    @Test
    fun differentUsers_haveIndependentDrafts() = runBlocking {
        repository.saveDraft("user-1", ProfileSetupState(name = "Ada"))
        repository.saveDraft("user-2", ProfileSetupState(name = "Grace"))

        assertEquals("Ada", repository.loadDraft("user-1")?.name)
        assertEquals("Grace", repository.loadDraft("user-2")?.name)
    }

    @Test
    fun loadDraft_fallsBackToNameStep_whenStepColumnIsUnrecognized() = runBlocking {
        // Simulates a stale draft row left over from a since-renamed/reordered
        // ProfileSetupStep entry - must not crash the resume path.
        dao.upsert(
            ProfileSetupDraftEntity(
                userId = "user-1",
                step = "SomeRemovedStep",
                name = "Ada",
                dateOfBirth = null,
                heightCm = 0,
                heightProvided = false,
                heightUnit = HeightUnit.CM.name,
                weightKg = 0,
                weightProvided = false,
                weightUnit = WeightUnit.KG.name,
                maritalStatus = null,
                lastPeriodStartDate = null,
                lastPeriodUnknown = false,
                averageCycleLengthDays = "",
                averagePeriodDurationDays = "",
                cycleRegularity = CycleRegularity.NotSure.name,
                updatedAtEpochMillis = 0L,
            ),
        )

        assertEquals(ProfileSetupStep.Name, repository.loadDraft("user-1")?.step)
    }
}
