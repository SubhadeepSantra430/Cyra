package subha.app.cyra.core.di

/**
 * Swift-callable entry point (SKIE exposes this as an ordinary Swift `object` with
 * static-like members). Call `KoinHelper.shared.doInitKoin()` exactly once, from
 * `iOSApp.init()`, before any SwiftUI view tries to resolve a shared ViewModel.
 *
 * There's deliberately no generic `get<T>()` helper here: reified inline generic
 * functions have no exported symbol at all in the produced Objective-C header, so they
 * cannot be called from Swift regardless of SKIE. Instead, each feature's `di` package
 * should expose one concrete, non-generic Swift-facing accessor per ViewModel, e.g.:
 *
 * ```kotlin
 * // feature/auth/di/AuthModule.kt
 * fun provideLoginViewModel(): LoginViewModel = KoinPlatform.getKoin().get()
 * ```
 *
 * which Swift then calls directly: `let vm = provideLoginViewModel()`.
 */
object KoinHelper {
    fun doInitKoin() {
        initKoin(iosPlatformModule)
    }
}
