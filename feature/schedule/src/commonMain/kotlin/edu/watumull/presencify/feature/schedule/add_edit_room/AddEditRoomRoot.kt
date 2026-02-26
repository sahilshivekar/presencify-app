package edu.watumull.presencify.feature.schedule.add_edit_room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditRoomRoot(
    onNavigateBack: () -> Unit,
    viewModel: AddEditRoomViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AddEditRoomEvent.NavigateBack -> onNavigateBack()
        }
    }

    AddEditRoomScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
