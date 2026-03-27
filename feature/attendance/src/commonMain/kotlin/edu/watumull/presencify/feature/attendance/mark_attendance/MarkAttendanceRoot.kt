package edu.watumull.presencify.feature.attendance.mark_attendance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MarkAttendanceRoot(
    onNavigateBack: () -> Unit,
    onNavigateToDynamicQR: (String) -> Unit,
    onNavigateToGroupPhotoScan: (String) -> Unit,
) {
    val viewModel = koinViewModel<MarkAttendanceViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            MarkAttendanceEvent.NavigateBack -> onNavigateBack()
            is MarkAttendanceEvent.NavigateToDynamicQR -> onNavigateToDynamicQR(event.attendanceId)
            is MarkAttendanceEvent.NavigateToGroupPhotoScan -> onNavigateToGroupPhotoScan(event.attendanceId)
        }
    }

    MarkAttendanceScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
