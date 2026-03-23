package edu.watumull.presencify.feature.attendance.add_student_biometrics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddStudentBiometricsRoot(
    onNavigateBack: () -> Unit,
    viewModel: AddStudentBiometricsViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AddStudentBiometricsEvent.NavigateBack -> onNavigateBack()
        }
    }

    AddStudentBiometricsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
