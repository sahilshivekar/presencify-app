package edu.watumull.presencify.feature.users.assign_unassign_student_to_semester

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssignUnassignStudentToSemesterRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchStudent: (semesterId: String, branchId: String) -> Unit,
) {
    val viewModel = koinViewModel<AssignUnassignStudentToSemesterViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AssignUnassignStudentToSemesterEvent.NavigateBack -> {
                onNavigateBack()
            }

            is AssignUnassignStudentToSemesterEvent.NavigateToSearchStudent -> {
                onNavigateToSearchStudent(event.semesterId, event.branchId)
            }
        }
    }

    AssignUnassignStudentToSemesterScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
