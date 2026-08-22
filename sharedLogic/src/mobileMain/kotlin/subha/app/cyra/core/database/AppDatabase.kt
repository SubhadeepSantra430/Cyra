package subha.app.cyra.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import subha.app.cyra.feature.profilesetup.data.ProfileSetupDraftDao
import subha.app.cyra.feature.profilesetup.data.ProfileSetupDraftEntity

/**
 * The single physical Room database file, logically partitioned by package ownership
 * of entities/DAOs - each feature adds its own entities here as it's built (e.g.
 * `DailyLogEntity`, `CycleEntryEntity`), bump [version] and add a migration when doing so.
 *
 * [SyncMetadataEntity] is the first real entity: infrastructure for the offline-first
 * sync strategy (see plan §Data layer), useful from the very first synced feature onward.
 *
 * [ProfileSetupDraftEntity] (v2) is the first feature to actually build on that
 * offline-first intent - the local, pre-submission mirror of the profile-setup flow.
 */
@Database(
    entities = [SyncMetadataEntity::class, ProfileSetupDraftEntity::class],
    version = 2,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun syncMetadataDao(): SyncMetadataDao
    abstract fun profileSetupDraftDao(): ProfileSetupDraftDao
}

/**
 * Required by Room on non-Android KMP targets (no reflection-based no-arg
 * instantiation there). Do NOT hand-write `actual` implementations for
 * iosArm64Main/iosSimulatorArm64Main - Room's KSP compiler generates them.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

internal const val DATABASE_FILE_NAME = "cyra.db"

/**
 * Attaches the bundled SQLite driver (works identically on Android/iOS/JVM) and builds.
 * Called from the platform Koin module once it has constructed the platform-appropriate
 * [RoomDatabase.Builder] (Android needs a Context, iOS needs the documents-directory path -
 * see `core/di/PlatformModule.android.kt` / `.ios.kt`).
 *
 * `fallbackToDestructiveMigration` is a standing house policy here, not a one-off: this
 * app is pre-release (no real user data at risk yet) and no migration path is written
 * for any version bump so far - without this, bumping [version] with a schema change
 * throws at runtime for anyone with an existing DB file on disk, not just "no clean
 * upgrade." Replace with real migrations once the app ships to real users.
 */
fun RoomDatabase.Builder<AppDatabase>.buildDatabase(): AppDatabase =
    setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
