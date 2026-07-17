package edu.watumull.presencify.feature.attendance.scan_qr

sealed interface ScanQrAction {
    data object NavigateBack : ScanQrAction
    data class Scanned(val content: String) : ScanQrAction
    data object ScanFailed : ScanQrAction
    data class CameraZoomLevelChange(val zoomLevel: Float) : ScanQrAction
}
