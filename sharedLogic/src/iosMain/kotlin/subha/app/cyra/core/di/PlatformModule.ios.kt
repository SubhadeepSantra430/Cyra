package subha.app.cyra.core.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask
import subha.app.cyra.core.database.AppDatabase
import subha.app.cyra.core.database.DATABASE_FILE_NAME

/**
 * The iOS half of "platformModule supplies what differs" (architecture plan §DI
 * Bootstrap) - a documents-directory-backed Room builder and NSUserDefaults-backed
 * Settings. Passed into `initKoin(platformModule)` from `KoinHelper.doInitKoin()`.
 */
val iosPlatformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        Room.databaseBuilder<AppDatabase>(
            name = documentDirectoryPath() + "/" + DATABASE_FILE_NAME,
        )
    }

    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<FlowSettings> { (get<Settings>() as NSUserDefaultsSettings).toFlowSettings() }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectoryPath(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path) { "Could not resolve iOS documents directory" }
}
