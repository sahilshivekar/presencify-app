package edu.watumull.presencify.feature.schedule.room_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoomDetailsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToEditRoom: (String) -> Unit,
    viewModel: RoomDetailsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RoomDetailsEvent.NavigateBack -> onNavigateBack()
            is RoomDetailsEvent.NavigateToEditRoom -> onNavigateToEditRoom(event.roomId)
        }
    }

    RoomDetailsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
