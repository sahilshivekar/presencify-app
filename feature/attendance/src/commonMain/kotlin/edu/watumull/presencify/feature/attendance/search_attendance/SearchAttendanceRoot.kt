package edu.watumull.presencify.feature.attendance.search_attendance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchAttendanceRoot(
    onNavigateBack: () -> Unit,
    onNavigateToAttendanceDetails: (String) -> Unit
) {
    val viewModel = koinViewModel<SearchAttendanceViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SearchAttendanceEvent.NavigateBack -> onNavigateBack()
            is SearchAttendanceEvent.NavigateToAttendanceDetails -> onNavigateToAttendanceDetails(event.attendanceId)
        }
    }

    SearchAttendanceScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
