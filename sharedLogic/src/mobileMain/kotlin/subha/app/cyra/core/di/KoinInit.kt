package subha.app.cyra.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Single entry point both platforms call at app start. [platformModule] is the one
 * piece each platform must supply differently (Room builder + Settings backing store -
 * see `PlatformModule.android.kt` / `.ios.kt`); every feature module is listed here once
 * so adding a new feature is a one-line addition, not a per-platform change.
 *
 * Android calls this from `CyraApplication.onCreate()` wrapped in `startKoin { ... }`
 * (needs `androidContext()`/`androidLogger()` first - see that file). iOS calls this
 * directly from `KoinHelper.doInitKoin()`, no extra wrapping needed.
 */
fun sharedKoinModules(platformModule: Module): List<Module> = listOf(
    platformModule,
    coreModule,
    // feature modules are appended here as each one is built, e.g.:
    // authModule, onboardingModule, cycleModule, calendarModule, dailyLogModule,
    // notificationsModule, aiChatModule, insightsModule, reportsModule, profileModule,
)

/** Convenience for platforms that don't need extra Koin configuration beyond the modules. */
fun initKoin(platformModule: Module, appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedKoinModules(platformModule))
    }
}
