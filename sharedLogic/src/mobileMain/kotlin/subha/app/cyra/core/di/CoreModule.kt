package subha.app.cyra.core.di

import androidx.room.RoomDatabase
import org.koin.dsl.module
import subha.app.cyra.core.database.AppDatabase
import subha.app.cyra.core.database.buildDatabase
import subha.app.cyra.core.datastore.AppSettings
import subha.app.cyra.core.network.createHttpClient
import subha.app.cyra.core.security.AppLockRepository
import subha.app.cyra.core.security.AppLockState
import subha.app.cyra.core.security.FieldCrypto

/**
 * Platform-independent singletons every feature module can depend on. Combined with a
 * platform module (`androidPlatformModule`/`iosPlatformModule`) in [initKoin].
 */
val coreModule = module {
    single<AppDatabase> { get<RoomDatabase.Builder<AppDatabase>>().buildDatabase() }
    single { get<AppDatabase>().syncMetadataDao() }

    single { AppSettings(get(), get()) }
    single { FieldCrypto() }
    single { AppLockState(get()) }
    single { AppLockRepository(get()) }

    single { createHttpClient() }
}
