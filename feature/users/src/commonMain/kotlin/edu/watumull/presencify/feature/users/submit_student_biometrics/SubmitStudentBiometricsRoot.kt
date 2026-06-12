package edu.watumull.presencify.feature.users.submit_student_biometrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SubmitStudentBiometricsRoot(
    onNavigateBack: () -> Unit,
    viewModel: SubmitStudentBiometricsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SubmitStudentBiometricsEvent.NavigateBack -> onNavigateBack()
        }
    }

    SubmitStudentBiometricsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
