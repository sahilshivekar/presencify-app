package edu.watumull.presencify.feature.users.review_student_biometrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReviewStudentBiometricsRoot(
    onNavigateBack: () -> Unit,
    viewModel: ReviewStudentBiometricsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ReviewStudentBiometricsEvent.NavigateBack -> onNavigateBack()
        }
    }

    ReviewStudentBiometricsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
