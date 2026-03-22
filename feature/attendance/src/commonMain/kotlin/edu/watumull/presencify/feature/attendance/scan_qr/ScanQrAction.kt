package edu.watumull.presencify.feature.attendance.scan_qr

import edu.watumull.presencify.core.presentation.navigation.NavRoute

sealed interface ScanQrAction {
    data object NavigateBack : ScanQrAction
    data class Scanned(val content: String) : ScanQrAction
    data object ScanFailed : ScanQrAction
}
