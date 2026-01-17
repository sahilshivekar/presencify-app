package edu.watumull.presencify.feature.users.search_teacher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchTeacherRoot(
    onNavigateBack: () -> Unit,
    onNavigateToTeacherDetails: (String) -> Unit = {},
    onNavigateToAddEditTeacher: (String?) -> Unit
) {
    val viewModel: SearchTeacherViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is SearchTeacherEvent.NavigateBack -> onNavigateBack()
            is SearchTeacherEvent.NavigateToStaffDetails -> {
                onNavigateToTeacherDetails(event.teacherId)
            }
            is SearchTeacherEvent.NavigateBackWithSelection -> {
                // For now, just navigate back
                // In the future, this could pass selected teacher IDs back to caller
                onNavigateBack()
            }
            is SearchTeacherEvent.NavigateToAddEditStaff -> {
                onNavigateToAddEditTeacher(null)
            }
        }
    }

    SearchTeacherScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}

