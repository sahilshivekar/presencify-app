package edu.watumull.presencify.feature.attendance.group_photo_scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog

@Composable
fun GroupPhotoScanCameraScreen(
    state: GroupPhotoScanState,
    onAction: (GroupPhotoScanAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    PresencifyScaffold(
        backPress = onNavigateBack,
        topBarTitle = "Capture classroom photos",
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            GroupPhotoScanCamera(
                onImageCaptured = { bytes ->
                    onAction(GroupPhotoScanAction.AddImages(listOf(bytes)))
                },
            )

            // Optional helper text overlay (kept minimal to avoid cluttering camera)
            Text(
                text = "Tap the capture button to add photos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
            )
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message?.asString() ?: "",
            onConfirm = { onAction(GroupPhotoScanAction.DismissDialog) },
            onDismiss = { onAction(GroupPhotoScanAction.DismissDialog) },
        )
    }
}
