package edu.watumull.presencify.feature.schedule.class_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClassDetailsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToEditClass: (timetableId: String, classId: String) -> Unit,
    onNavigateToCreateAttendanceSheet: (classId: String) -> Unit,
    viewModel: ClassDetailsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClassDetailsEvent.NavigateBack -> onNavigateBack()
            is ClassDetailsEvent.NavigateToEditClass -> onNavigateToEditClass(event.timetableId, event.classId)
            is ClassDetailsEvent.NavigateToCreateAttendanceSheet -> onNavigateToCreateAttendanceSheet(event.classId)
        }
    }

    ClassDetailsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
