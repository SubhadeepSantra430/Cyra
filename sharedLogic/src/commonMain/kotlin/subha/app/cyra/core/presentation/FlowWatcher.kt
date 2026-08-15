package subha.app.cyra.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Swift-interop fallback for the rare case SKIE ever needs to be disabled again (see the
 * `skie {}` block in sharedLogic/build.gradle.kts - it's currently enabled and working).
 * Without SKIE, Swift can't consume a raw `StateFlow`/`Flow` as an `AsyncSequence` -
 * Kotlin/Native's plain Objective-C export turns `collect` into a completion-handler
 * shaped call that isn't useful for an ongoing stream. [watch] bridges the gap with the
 * classic pre-SKIE pattern: a plain callback plus a cancellation handle, both of which
 * export to Swift cleanly with no compiler plugin involved. With SKIE enabled, prefer
 * `for await state in viewModel.uiState` directly - this is a fallback, not the default.
 *
 * ```swift
 * let cancellable = viewModel.uiState.watch { state in
 *     self.state = state
 * }
 * // later, e.g. in deinit:
 * cancellable.cancel()
 * ```
 */
fun <T> Flow<T>.watch(onEach: (T) -> Unit): Cancellable {
    val scope = CoroutineScope(Dispatchers.Main + Job())
    scope.launch {
        collectLatest { onEach(it) }
    }
    return Cancellable(scope)
}

class Cancellable internal constructor(private val scope: CoroutineScope) {
    fun cancel() {
        scope.cancel()
    }
}
