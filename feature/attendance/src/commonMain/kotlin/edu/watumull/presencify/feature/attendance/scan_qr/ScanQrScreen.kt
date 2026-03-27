package edu.watumull.presencify.feature.attendance.scan_qr

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun ScanQrScreen(
    state: ScanQrState,
    onAction: (ScanQrAction) -> Unit
) {
    if (!state.isQrScanSuccessful) {
        PresencifyScaffold(
            backPress = { onAction(ScanQrAction.NavigateBack) },
            topBarTitle = "Scan QR"
        ) { paddingValues ->
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
                    }
                )
            }
        }
    }
}
