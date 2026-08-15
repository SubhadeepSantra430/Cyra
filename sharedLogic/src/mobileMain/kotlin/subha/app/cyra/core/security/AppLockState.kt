package subha.app.cyra.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import subha.app.cyra.core.datastore.AppSettings

/**
 * Shared "is the app locked" state that both platforms' root Composable/View gate on.
 * The biometric *prompt* itself is unavoidably native (`androidx.biometric.BiometricPrompt`
 * vs `LAContext`/`LocalAuthentication`), but both platforms report success back through
 * the same [AppLockRepository.unlock], so the gating logic only lives once.
 */
class AppLockState(private val appSettings: AppSettings) {
    private val _isLocked = MutableStateFlow(appSettings.isBiometricEnabled)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    internal fun setLocked(locked: Boolean) {
        _isLocked.value = locked
    }
}

/**
 * Called by each platform's native biometric-success callback. Neither platform should
 * flip [AppLockState] directly - always go through here so unlocking is auditable/testable
 * in commonTest with a fake.
 */
class AppLockRepository(private val appLockState: AppLockState) {
    fun unlock() = appLockState.setLocked(false)
    fun lock() = appLockState.setLocked(true)
}
