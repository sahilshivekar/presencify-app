package edu.watumull.presencify.feature.attendance.aggregate_analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AggregateAttendanceAnalyticsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchAttendanceForCourse: (String) -> Unit,
) {
    val viewModel: AggregateAttendanceAnalyticsViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AggregateAttendanceAnalyticsEvent.NavigateBack -> onNavigateBack()
            is AggregateAttendanceAnalyticsEvent.NavigateToSearchAttendanceForCourse ->
                onNavigateToSearchAttendanceForCourse(event.courseId)
        }
    }

    AggregateAttendanceAnalyticsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
