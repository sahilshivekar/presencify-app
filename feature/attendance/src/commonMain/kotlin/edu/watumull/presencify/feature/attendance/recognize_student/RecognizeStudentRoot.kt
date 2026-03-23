package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecognizeStudentRoot(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: RecognizeStudentViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is RecognizeStudentEvent.NavigateBack -> onNavigateBack()
            is RecognizeStudentEvent.MapsToSuccess -> onSuccess()
        }
    }

    RecognizeStudentScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
