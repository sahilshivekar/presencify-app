package edu.watumull.presencify.feature.attendance.group_photo_scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken

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
                    .padding(top = DesignToken.spacing.lg),
            )
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            onDismiss = { onAction(GroupPhotoScanAction.DismissDialog) },
        )
    }
}
