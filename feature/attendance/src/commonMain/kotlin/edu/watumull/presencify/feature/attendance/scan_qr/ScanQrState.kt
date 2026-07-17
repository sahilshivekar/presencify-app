package edu.watumull.presencify.feature.attendance.scan_qr

import edu.watumull.presencify.core.presentation.UiText

data class ScanQrState(
    val viewState: ViewState = ViewState.Content,
    val cameraZoomLevel: Float = 1f,
    val isLoading: Boolean = false,
    val lastScannedContent: String? = null,
    val isQrScanSuccessful: Boolean = false
) {
    sealed interface ViewState {
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }
}
