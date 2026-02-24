package edu.watumull.presencify.feature.users.modify_student_batch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ModifyStudentBatchRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchStudent: (
        batchId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int,
        newStartDate: String
    ) -> Unit,
) {
    val viewModel = koinViewModel<ModifyStudentBatchViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ModifyStudentBatchEvent.NavigateBack -> {
                onNavigateBack()
            }

            is ModifyStudentBatchEvent.NavigateToSearchStudent -> {
                onNavigateToSearchStudent(
                    event.batchId,
                    event.branchId,
                    event.academicStartYear,
                    event.academicEndYear,
                    event.semesterNumber,
                    event.newStartDate
                )
            }
        }
    }

    ModifyStudentBatchScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
