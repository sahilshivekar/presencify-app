package edu.watumull.presencify.feature.users.assign_unassign_student_to_batch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssignUnassignStudentToBatchRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchStudent: (
        batchId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int
    ) -> Unit,
) {
    val viewModel = koinViewModel<AssignUnassignStudentToBatchViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AssignUnassignStudentToBatchEvent.NavigateBack -> {
                onNavigateBack()
            }

            is AssignUnassignStudentToBatchEvent.NavigateToSearchStudent -> {
                onNavigateToSearchStudent(
                    event.batchId,
                    event.branchId,
                    event.academicStartYear,
                    event.academicEndYear,
                    event.semesterNumber
                )
            }
        }
    }

    AssignUnassignStudentToBatchScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
