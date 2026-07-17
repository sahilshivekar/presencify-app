package edu.watumull.presencify.feature.attendance.scan_qr

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun ScanQrScreen(
    state: ScanQrState,
    onAction: (ScanQrAction) -> Unit
) {
    PresencifyScaffold(
        backPress = { onAction(ScanQrAction.NavigateBack) },
        topBarTitle = "Scan QR"
    ) { paddingValues ->
        when (state.viewState) {
            is ScanQrState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is ScanQrState.ViewState.Content -> {
                if (!state.isQrScanSuccessful) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        QrScanner(
                            modifier = Modifier.fillMaxSize(),
                            flashlightOn = false,
                            openImagePicker = false,
                            onCompletion = { result ->
                                onAction(ScanQrAction.Scanned(result))
                            },
                            imagePickerHandler = {},
                            cameraLens = CameraLens.Back,
                            onFailure = {
                                Logger.d { "QR scan failed: $it" }
                                onAction(ScanQrAction.ScanFailed)
                            },
                            zoomLevel = state.cameraZoomLevel
                        )
                        Slider(
                            value = state.cameraZoomLevel,
                            onValueChange = { onAction(ScanQrAction.CameraZoomLevelChange(it)) },
                            valueRange = 1f..5f,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(DesignToken.spacing.xl)
                        )
                    }
                }
            }
        }
    }
}
