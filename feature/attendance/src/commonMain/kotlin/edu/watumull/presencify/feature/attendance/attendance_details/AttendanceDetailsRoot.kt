package edu.watumull.presencify.feature.attendance.attendance_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AttendanceDetailsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToEditAttendance: (String) -> Unit
) {
    val viewModel = koinViewModel<AttendanceDetailsViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AttendanceDetailsEvent.NavigateBack -> onNavigateBack()
            is AttendanceDetailsEvent.NavigateToEditAttendance -> onNavigateToEditAttendance(event.attendanceId)
        }
    }

    AttendanceDetailsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
