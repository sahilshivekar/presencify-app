package edu.watumull.presencify.feature.attendance.dynamic_qr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DynamicQRRoot(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    val viewModel = koinViewModel<DynamicQRViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            DynamicQREvent.NavigateBack -> onNavigateBack()
            is DynamicQREvent.NavigateToDetails -> onNavigateToDetails(event.attendanceId)
        }
    }

    DynamicQRScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
