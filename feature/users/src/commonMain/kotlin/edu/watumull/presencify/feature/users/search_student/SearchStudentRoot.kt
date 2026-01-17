package edu.watumull.presencify.feature.users.search_student

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchStudentRoot(
    onNavigateBack: () -> Unit,
    onNavigateToStudentDetails: (String) -> Unit = {},
    onNavigateToAddEditStudent: (String?) -> Unit = {},
) {
    val viewModel: SearchStudentViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is SearchStudentEvent.NavigateBack -> onNavigateBack()
            is SearchStudentEvent.NavigateToStudentDetails -> {
                onNavigateToStudentDetails(event.studentId)
            }
            is SearchStudentEvent.NavigateBackWithSelection -> {
                // For now, just navigate back
                // In the future, this could pass selected student IDs back to caller
                onNavigateBack()
            }
            is SearchStudentEvent.NavigateToAddEditStudent -> {
                onNavigateToAddEditStudent(null)
            }
        }
    }

    SearchStudentScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}

