package edu.watumull.presencify.feature.schedule.search_class

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchClassRoot(
    onNavigateBack: () -> Unit,
    onNavigateToClassDetails: (String) -> Unit,
    onNavigateToCreateAttendanceSheet: (String) -> Unit,
    viewModel: SearchClassViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SearchClassEvent.NavigateBack -> onNavigateBack()
            is SearchClassEvent.NavigateToClassDetails -> onNavigateToClassDetails(event.classId)
            is SearchClassEvent.NavigateToCreateAttendanceSheet -> onNavigateToCreateAttendanceSheet(event.classId)
        }
    }

    SearchClassScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
