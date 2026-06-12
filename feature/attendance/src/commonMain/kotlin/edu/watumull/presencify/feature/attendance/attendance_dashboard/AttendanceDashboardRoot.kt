package edu.watumull.presencify.feature.attendance.attendance_dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AttendanceDashboardRoot(
    onNavigateBack: () -> Unit,
    onNavigateToStudentAttendanceAnalytics: () -> Unit,
    onNavigateToAggregateAttendanceAnalytics: () -> Unit,
    onNavigateToSearchAttendance: () -> Unit,
    onNavigateToCreateAttendance: () -> Unit,
    onNavigateToDefaulters: () -> Unit,
    viewModel: AttendanceDashboardViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AttendanceDashboardEvent.NavigateBack -> onNavigateBack()
            AttendanceDashboardEvent.NavigateToStudentAttendanceAnalytics -> onNavigateToStudentAttendanceAnalytics()
            AttendanceDashboardEvent.NavigateToAggregateAttendanceAnalytics -> onNavigateToAggregateAttendanceAnalytics()
            AttendanceDashboardEvent.NavigateToSearchAttendance -> onNavigateToSearchAttendance()
            AttendanceDashboardEvent.NavigateToCreateAttendance -> onNavigateToCreateAttendance()
            AttendanceDashboardEvent.NavigateToDefaulters -> onNavigateToDefaulters()
        }
    }

    AttendanceDashboardScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
