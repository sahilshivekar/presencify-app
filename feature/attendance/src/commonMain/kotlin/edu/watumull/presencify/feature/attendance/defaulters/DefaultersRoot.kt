package edu.watumull.presencify.feature.attendance.defaulters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DefaultersRoot(
    onNavigateBack: () -> Unit,
) {
    val viewModel: DefaultersViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            DefaultersEvent.NavigateBack -> onNavigateBack()
        }
    }

    DefaultersScreen(
        state = state,
        onAction = viewModel::trySendAction,
        onNavigateBack = onNavigateBack
    )
}
