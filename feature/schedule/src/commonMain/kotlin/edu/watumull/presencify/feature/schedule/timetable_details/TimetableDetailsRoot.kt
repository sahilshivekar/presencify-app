package edu.watumull.presencify.feature.schedule.timetable_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimetableDetailsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToEditTimetable: (String) -> Unit,
    onNavigateToAddClass: (timetableId: String, classId: String?) -> Unit,
    onNavigateToClassDetails: (String) -> Unit,
    viewModel: TimetableDetailsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            TimetableDetailsEvent.NavigateBack -> onNavigateBack()
            is TimetableDetailsEvent.NavigateToEditTimetable -> onNavigateToEditTimetable(event.timetableId)
            is TimetableDetailsEvent.NavigateToAddClass -> onNavigateToAddClass(event.timetableId, null)
            is TimetableDetailsEvent.NavigateToClassDetails -> onNavigateToClassDetails(event.classId)
        }
    }

    TimetableDetailsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
