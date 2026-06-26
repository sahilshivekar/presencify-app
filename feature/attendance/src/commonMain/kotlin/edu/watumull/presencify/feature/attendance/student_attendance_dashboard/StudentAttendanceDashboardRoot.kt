package edu.watumull.presencify.feature.attendance.student_attendance_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentAttendanceDashboardRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchAttendanceForCourse: (courseId: String, studentId: String) -> Unit,
    onNavigateToScanQr: () -> Unit,
) {
    val viewModel: StudentAttendanceDashboardViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            StudentAttendanceDashboardEvent.NavigateBack -> onNavigateBack()
            is StudentAttendanceDashboardEvent.NavigateToSearchAttendanceForCourse -> {
                onNavigateToSearchAttendanceForCourse(event.courseId, event.studentId)
            }
            StudentAttendanceDashboardEvent.NavigateToScanQr -> onNavigateToScanQr()
        }
    }

    StudentAttendanceDashboardScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
