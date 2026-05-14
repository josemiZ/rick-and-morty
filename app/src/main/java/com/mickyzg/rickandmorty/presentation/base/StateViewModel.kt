package com.mickyzg.rickandmorty.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import androidx.lifecycle.viewModelScope

/**
 * Base ViewModel for the MVI architecture used across the app.
 *
 * Type parameters:
 *  - [S] the immutable UI state that the screen observes.
 *  - [A] the sealed interface representing every user action or intent.
 *
 * Contract:
 *  - [state] is the **single source of truth** exposed to the UI.
 *  - [publish] is the **single entry point** through which the UI sends actions.
 *    All other ViewModel methods must be `private`.
 *  - [update] is the **only way** to mutate state, ensuring all transitions are
 *    traceable and reducible to a pure function `(S) -> S`.
 *
 * Example:
 * ```kotlin
 * @HiltViewModel
 * class CharacterListViewModel @Inject constructor(
 *     private val repository: CharacterRepository
 * ) : StateViewModel<CharacterListUiState, CharacterListAction>(CharacterListUiState()) {
 *
 *     override fun publish(action: CharacterListAction) = when (action) {
 *         is CharacterListAction.LoadMore       -> loadMore()
 *         is CharacterListAction.Refresh        -> refresh()
 *         is CharacterListAction.SearchChanged  -> searchChanged(action.query)
 *     }
 *
 *     private fun loadMore() { ... }
 * }
 * ```
 *
 * @param initialState the starting value of [state] before any action is published.
 */
abstract class StateViewModel<S, A>(initialState: S) : ViewModel() {

    protected val _state = MutableStateFlow(initialState)

    /** Immutable state stream observed by the UI via [collectAsStateWithLifecycle]. */
    val state: StateFlow<S> = _state.asStateFlow()

    /**
     * Single public entry point for the UI to communicate with this ViewModel.
     *
     * Implementations must use an exhaustive `when` over [action] and delegate
     * to private functions — no business logic should live directly inside [publish].
     */
    abstract fun publish(action: A)

    /**
     * Applies [reducer] to the current state and emits the result atomically.
     * All state mutations must go through this function.
     */
    protected fun update(reducer: (S) -> S) {
        _state.update(reducer)
    }

    /**
     * Convenience wrapper around [viewModelScope.launch] to keep coroutine
     * launches readable inside action handler functions.
     * Returns the underlying [Job] so callers can cancel it if needed.
     */
    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job =
        viewModelScope.launch(block = block)
}
