package edu.watumull.presencify.feature.schedule.add_edit_class

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditClassRoot(
    onNavigateBack: () -> Unit,
    viewModel: AddEditClassViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AddEditClassEvent.NavigateBack -> onNavigateBack()
        }
    }

    AddEditClassScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
