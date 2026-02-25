package edu.watumull.presencify.feature.users.mark_unmark_student_dropout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MarkUnmarkStudentAsDropoutRoot(
    onNavigateBack: () -> Unit,
    onNavigateToSearchStudent: (dropoutAcademicStartYear: Int, dropoutAcademicEndYear: Int) -> Unit,
    viewModel: MarkUnmarkStudentAsDropoutViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            MarkUnmarkStudentAsDropoutEvent.NavigateBack -> onNavigateBack()
            is MarkUnmarkStudentAsDropoutEvent.NavigateToSearchStudent -> {
                onNavigateToSearchStudent(
                    event.dropoutAcademicStartYear,
                    event.dropoutAcademicEndYear
                )
            }
        }
    }

    MarkUnmarkStudentAsDropoutScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
