package edu.watumull.presencify.feature.schedule.search_room

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchRoomRoot(
    onNavigateBack: () -> Unit,
    onNavigateToRoomDetails: (String) -> Unit,
    onNavigateToAddEditRoom: () -> Unit,
    viewModel: SearchRoomViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SearchRoomEvent.NavigateBack -> onNavigateBack()
            is SearchRoomEvent.NavigateToRoomDetails -> onNavigateToRoomDetails(event.roomId)
            SearchRoomEvent.NavigateToAddEditRoom -> onNavigateToAddEditRoom()
        }
    }

    SearchRoomScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
