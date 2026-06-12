package edu.watumull.presencify.feature.users.student_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentDetailsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToEditStudent: (String) -> Unit,
    onNavigateToUpdatePassword: () -> Unit,
    onNavigateToSubmitBiometrics: () -> Unit,
    onNavigateToReviewBiometrics: (String) -> Unit,
) {
    val viewModel: StudentDetailsViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is StudentDetailsEvent.NavigateBack -> onNavigateBack()
            is StudentDetailsEvent.NavigateToEditStudent -> onNavigateToEditStudent(event.studentId)
            is StudentDetailsEvent.NavigateToUpdatePassword -> onNavigateToUpdatePassword()
            is StudentDetailsEvent.NavigateToSubmitBiometrics -> onNavigateToSubmitBiometrics()
            is StudentDetailsEvent.NavigateToReviewBiometrics -> onNavigateToReviewBiometrics(event.studentId)
        }
    }

    StudentDetailsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
