package edu.watumull.presencify.feature.attendance.create_attendance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateAttendanceRoot(
    onNavigateBack: () -> Unit,
    onNavigateToMarkAttendance: (String) -> Unit,
    viewModel: CreateAttendanceViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            CreateAttendanceEvent.NavigateBack -> onNavigateBack()
            is CreateAttendanceEvent.NavigateToMarkAttendance -> onNavigateToMarkAttendance(event.attendanceId)
        }
    }

    CreateAttendanceScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
