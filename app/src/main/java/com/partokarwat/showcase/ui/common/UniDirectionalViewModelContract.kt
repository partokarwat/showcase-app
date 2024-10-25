package com.partokarwat.showcase.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface UniDirectionalViewModelContract<STATE, INTENT, EVENT> {
    val state: StateFlow<STATE>
    val event: SharedFlow<EVENT>

    fun intent(intent: INTENT)
}

@Composable
inline fun <reified STATE, INTENT, EVENT> use(
    viewModel: UniDirectionalViewModelContract<STATE, INTENT, EVENT>,
): StateDispatchEffect<STATE, INTENT, EVENT> {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val dispatch: (INTENT) -> Unit = { intent ->
        viewModel.intent(intent)
    }

    return StateDispatchEffect(
        state = state,
        eventFlow = viewModel.event,
        dispatch = dispatch,
    )
}

data class StateDispatchEffect<STATE, INTENT, EVENT>(
    val state: STATE,
    val dispatch: (INTENT) -> Unit,
    val eventFlow: SharedFlow<EVENT>,
)
