package edu.watumull.presencify.feature.schedule.search_timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchTimetableRoot(
    onNavigateBack: () -> Unit,
    onNavigateToTimetableDetails: (String) -> Unit,
    onNavigateToAddEditTimetable: () -> Unit,
    viewModel: SearchTimetableViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SearchTimetableEvent.NavigateBack -> onNavigateBack()
            is SearchTimetableEvent.NavigateToTimetableDetails -> onNavigateToTimetableDetails(event.timetableId)
            SearchTimetableEvent.NavigateToAddTimetable -> onNavigateToAddEditTimetable()
        }
    }

    SearchTimetableScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
