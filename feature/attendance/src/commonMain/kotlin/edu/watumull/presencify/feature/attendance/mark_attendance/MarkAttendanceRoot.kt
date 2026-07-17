package edu.watumull.presencify.feature.attendance.mark_attendance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MarkAttendanceRoot(
    onNavigateBackFromMarkAttendanceScreen: (String) -> Unit,
) {
    val viewModel = koinViewModel<MarkAttendanceViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is MarkAttendanceEvent.NavigateBack -> onNavigateBackFromMarkAttendanceScreen(event.attendanceId)
        }
    }

    MarkAttendanceScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
