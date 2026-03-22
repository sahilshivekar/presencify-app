package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecognizeStudentRoot(
    onNavigateBack: () -> Unit
) {
    val viewModel = koinViewModel<RecognizeStudentViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RecognizeStudentEvent.NavigateBack -> onNavigateBack()
        }
    }

    RecognizeStudentScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
