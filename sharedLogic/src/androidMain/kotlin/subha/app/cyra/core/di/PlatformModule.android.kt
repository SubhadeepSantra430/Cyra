package subha.app.cyra.core.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import subha.app.cyra.core.database.AppDatabase
import subha.app.cyra.core.database.DATABASE_FILE_NAME

/**
 * The Android half of "platformModule supplies what differs" (architecture plan §DI
 * Bootstrap) - a Context-backed Room builder and SharedPreferences-backed Settings.
 * Passed into `initKoin(platformModule)` from `CyraApplication.onCreate()`.
 */
val androidPlatformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        Room.databaseBuilder<AppDatabase>(
            context = androidContext(),
            name = androidContext().getDatabasePath(DATABASE_FILE_NAME).absolutePath,
        )
    }

    single<Settings> {
        val prefs = androidContext().getSharedPreferences("cyra_settings", 0)
        SharedPreferencesSettings(prefs)
    }
    single<FlowSettings> { (get<Settings>() as SharedPreferencesSettings).toFlowSettings() }
}
