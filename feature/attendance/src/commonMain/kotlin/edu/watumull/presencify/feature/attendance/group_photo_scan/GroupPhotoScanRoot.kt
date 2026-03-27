package edu.watumull.presencify.feature.attendance.group_photo_scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GroupPhotoScanRoot(
    onNavigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<GroupPhotoScanViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            GroupPhotoScanEvent.NavigateBack -> onNavigateBack()
        }
    }

    if (state.isCameraOpen) {
        GroupPhotoScanCameraScreen(
            state = state,
            onAction = viewModel::trySendAction,
            onNavigateBack = { viewModel.trySendAction(GroupPhotoScanAction.CloseCamera) },
        )
    } else {
        GroupPhotoScanScreen(
            state = state,
            onAction = viewModel::trySendAction,
        )
    }
}
