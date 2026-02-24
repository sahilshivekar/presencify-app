package edu.watumull.presencify.feature.users.assign_unassign_student_to_division

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AssignUnassignStudentToDivisionRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchStudent: (
        divisionId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int
    ) -> Unit,
) {
    val viewModel = koinViewModel<AssignUnassignStudentToDivisionViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AssignUnassignStudentToDivisionEvent.NavigateBack -> {
                onNavigateBack()
            }

            is AssignUnassignStudentToDivisionEvent.NavigateToSearchStudent -> {
                onNavigateToSearchStudent(
                    event.divisionId,
                    event.branchId,
                    event.academicStartYear,
                    event.academicEndYear,
                    event.semesterNumber
                )
            }
        }
    }

    AssignUnassignStudentToDivisionScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
