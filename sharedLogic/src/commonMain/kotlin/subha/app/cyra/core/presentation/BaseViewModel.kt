package subha.app.cyra.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base class for every shared feature ViewModel. Built on the multiplatform
 * `androidx.lifecycle.ViewModel`, so [viewModelScope] works identically on Android
 * (Koin's Android ViewModel factory) and iOS (Koin's plain KMP `viewModel { }` DSL).
 *
 * - [uiState] is the single source of truth for what a screen renders.
 * - [sideEffect] is for one-shot events a screen should react to exactly once
 *   (navigation, snackbars, etc.) - never re-delivered on recomposition/re-observation.
 *
 * iOS caveat: [ViewModel.onCleared] fires automatically on Android via the
 * ViewModelStore lifecycle. On iOS there is no such owner - the Swift-side
 * `VMStateObserver` wrapper (see iosApp/App/ViewModelWrapper.swift) must call
 * `viewModel.clear()`/equivalent from its own `deinit` once that wrapper exists.
 */
abstract class BaseViewModel<State : Any, Effect : Any>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<Effect>(extraBufferCapacity = 1)
    val sideEffect: SharedFlow<Effect> = _sideEffect.asSharedFlow()

    protected val currentState: State
        get() = _uiState.value

    protected fun setState(reducer: State.() -> State) {
        _uiState.update(reducer)
    }

    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch { _sideEffect.emit(effect) }
    }
}
