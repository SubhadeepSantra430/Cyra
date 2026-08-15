package subha.app.cyra.core.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * One row per syncable feature (e.g. "cycle", "dailylog", "profile") tracking the
 * offline-first sync state described in the architecture plan: rows written locally
 * are optimistic and PENDING until pushed to Firestore, then flipped to SYNCED.
 * [pendingWriteCount] lets a feature's SyncEngine know whether there's anything to
 * push without re-scanning its own tables.
 */
@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val featureKey: String,
    val lastSyncedAtEpochMillis: Long,
    val pendingWriteCount: Int,
)

@Dao
interface SyncMetadataDao {
    @Upsert
    suspend fun upsert(metadata: SyncMetadataEntity)

    @Query("SELECT * FROM sync_metadata WHERE featureKey = :featureKey")
    fun observe(featureKey: String): Flow<SyncMetadataEntity?>

    @Query("SELECT * FROM sync_metadata")
    fun observeAll(): Flow<List<SyncMetadataEntity>>
}
