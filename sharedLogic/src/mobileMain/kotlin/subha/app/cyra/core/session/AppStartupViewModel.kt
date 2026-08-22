package subha.app.cyra.core.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import subha.app.cyra.core.datastore.AppSettings

/**
 * Resolves [AppStartupDestination] exactly once per cold start - not a
 * `BaseViewModel<State, Effect>` since there's no ongoing state beyond "figure this out
 * once," unlike Auth's/ProfileSetup's per-screen ViewModels. `MainActivity.kt`'s
 * `CyraAppFlow`/`iOSApp.swift`'s `CyraRootView` hold their splash screen up until
 * [destination] resolves (`null` = still resolving), then layer their own local
 * "override" state on top for the transitions that happen *within* a session
 * afterward (auth completes, onboarding finishes, profile setup finishes) - this
 * ViewModel's job ends at the first decision.
 */
class AppStartupViewModel(
    private val sessionManager: SessionManager,
    private val appSettings: AppSettings,
) : ViewModel() {

    private val _destination = MutableStateFlow<AppStartupDestination?>(null)
    val destination: StateFlow<AppStartupDestination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            _destination.value = sessionManager.determineStartupDestination()
        }
    }

    /**
     * The one bit of "finishing this step persists something" logic that had nowhere
     * to live before - `OnboardingScreen`/`OnboardingView` (the marketing carousel) are
     * stateless, with no backing ViewModel of their own.
     */
    fun markOnboardingCarouselSeen() {
        appSettings.isOnboarded = true
    }
}
