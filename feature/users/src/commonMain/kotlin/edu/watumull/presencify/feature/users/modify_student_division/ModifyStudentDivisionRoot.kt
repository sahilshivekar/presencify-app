package edu.watumull.presencify.feature.users.modify_student_division

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ModifyStudentDivisionRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchStudent: (
        divisionId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int,
        newStartDate: String
    ) -> Unit,
) {
    val viewModel = koinViewModel<ModifyStudentDivisionViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ModifyStudentDivisionEvent.NavigateBack -> {
                onNavigateBack()
            }

            is ModifyStudentDivisionEvent.NavigateToSearchStudent -> {
                onNavigateToSearchStudent(
                    event.divisionId,
                    event.branchId,
                    event.academicStartYear,
                    event.academicEndYear,
                    event.semesterNumber,
                    event.newStartDate
                )
            }
        }
    }

    ModifyStudentDivisionScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
