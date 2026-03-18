package edu.watumull.presencify.feature.attendance.student_analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentAttendanceAnalyticsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchAttendanceForCourse: (courseId: String, studentId: String) -> Unit,
) {
    val viewModel: StudentAttendanceAnalyticsViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is StudentAttendanceAnalyticsEvent.NavigateBack -> onNavigateBack()
            is StudentAttendanceAnalyticsEvent.NavigateToSearchAttendanceForCourse ->
                onNavigateToSearchAttendanceForCourse(event.courseId, event.studentId)
        }
    }

    StudentAttendanceAnalyticsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
