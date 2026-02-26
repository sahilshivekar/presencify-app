package edu.watumull.presencify.feature.schedule.add_edit_timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditTimetableRoot(
    onNavigateBack: () -> Unit,
    onNavigateToTimetableDetails: (String) -> Unit,
    viewModel: AddEditTimetableViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AddEditTimetableEvent.NavigateBack -> onNavigateBack()
            is AddEditTimetableEvent.NavigateToTimetableDetails -> onNavigateToTimetableDetails(event.timetableId)
        }
    }

    AddEditTimetableScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
