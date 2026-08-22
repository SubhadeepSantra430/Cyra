package subha.app.cyra.core.datastore

import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Lightweight app-level flags only - theme, locale, onboarding status, whether the
 * biometric lock is enabled. Deliberately NOT for auth tokens/session data: GitLive's
 * Firebase Auth SDK persists its own session state natively per platform - don't
 * duplicate that here.
 *
 * Wraps `multiplatform-settings`; each platform supplies its own [Settings]/[FlowSettings]
 * instance to the constructor (Android: SharedPreferences-backed, iOS: NSUserDefaults-backed),
 * via the platform Koin module.
 */
class AppSettings(
    private val settings: Settings,
    private val flowSettings: FlowSettings,
) {
    companion object {
        private const val KEY_IS_ONBOARDED = "is_onboarded"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_THEME = "theme_mode" // "light" | "dark" | "system"
        private const val KEY_LAST_SYNCED_AT = "last_synced_at_epoch_millis"
        private const val KEY_PROFILE_SETUP_COMPLETED = "profile_setup_completed"
    }

    var isOnboarded: Boolean
        get() = settings.getBoolean(KEY_IS_ONBOARDED, false)
        set(value) = settings.putBoolean(KEY_IS_ONBOARDED, value)

    /**
     * The fast, offline-safe "did this user finish profile setup" check `SessionManager`
     * gates on at startup - set once `ProfileSetupViewModel.submitProfile()`'s Firestore
     * write actually succeeds (`ProfileRepository.saveProfile`'s `ProfileDocument` also
     * carries its own `profileSetupCompleted` field server-side; this is the local
     * cache of that same fact, not a duplicate source of truth for auth/session data -
     * see this class's own doc comment above).
     */
    var isProfileSetupCompleted: Boolean
        get() = settings.getBoolean(KEY_PROFILE_SETUP_COMPLETED, false)
        set(value) = settings.putBoolean(KEY_PROFILE_SETUP_COMPLETED, value)

    var isBiometricEnabled: Boolean
        get() = settings.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = settings.putBoolean(KEY_BIOMETRIC_ENABLED, value)

    var themeMode: String
        get() = settings.getString(KEY_THEME, "system")
        set(value) = settings.putString(KEY_THEME, value)

    var lastSyncedAtEpochMillis: Long
        get() = settings.getLong(KEY_LAST_SYNCED_AT, 0L)
        set(value) = settings.putLong(KEY_LAST_SYNCED_AT, value)

    val isBiometricEnabledFlow: Flow<Boolean>
        get() = flowSettings.getBooleanFlow(KEY_BIOMETRIC_ENABLED, false)

    val themeModeFlow: Flow<String>
        get() = flowSettings.getStringFlow(KEY_THEME, "system")
}
