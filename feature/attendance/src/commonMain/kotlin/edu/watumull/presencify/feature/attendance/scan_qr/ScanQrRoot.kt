package edu.watumull.presencify.feature.attendance.scan_qr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScanQrRoot(
    onNavigateBack: () -> Unit,
    onNavigateToRecognizeStudent: (String) -> Unit
) {
    val viewModel = koinViewModel<ScanQrViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ScanQrEvent.NavigateBack -> onNavigateBack()
            is ScanQrEvent.NavigateToRecognizeStudent -> onNavigateToRecognizeStudent(event.attendanceId)
        }
    }

    ScanQrScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
