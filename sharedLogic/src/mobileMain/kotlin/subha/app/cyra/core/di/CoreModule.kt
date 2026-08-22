package subha.app.cyra.core.di

import androidx.room.RoomDatabase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import subha.app.cyra.core.database.AppDatabase
import subha.app.cyra.core.database.buildDatabase
import subha.app.cyra.core.datastore.AppSettings
import subha.app.cyra.core.network.createHttpClient
import subha.app.cyra.core.security.AppLockRepository
import subha.app.cyra.core.security.AppLockState
import subha.app.cyra.core.security.FieldCrypto
import subha.app.cyra.core.session.AppStartupViewModel
import subha.app.cyra.core.session.SessionManager

/**
 * Platform-independent singletons every feature module can depend on. Combined with a
 * platform module (`androidPlatformModule`/`iosPlatformModule`) in [initKoin].
 */
val coreModule = module {
    single<AppDatabase> { get<RoomDatabase.Builder<AppDatabase>>().buildDatabase() }
    single { get<AppDatabase>().syncMetadataDao() }
    single { get<AppDatabase>().profileSetupDraftDao() }

    single { AppSettings(get(), get()) }
    single { FieldCrypto() }
    single { AppLockState(get()) }
    single { AppLockRepository(get()) }

    // Depends on AuthRepository (authModule) - fine, Koin resolves lazily regardless of
    // which module a dependency is registered in.
    single { SessionManager(get(), get()) }
    viewModel { AppStartupViewModel(get(), get()) }

    single { createHttpClient() }
}

// Swift can't call reified generic `get<T>()` (see KoinHelper.kt) - mirrors
// provideLoginViewModel() etc. in AuthModule.kt.
fun provideAppStartupViewModel(): AppStartupViewModel = KoinPlatform.getKoin().get()
