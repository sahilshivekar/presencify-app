package edu.watumull.presencify.feature.attendance.scan_qr

sealed interface ScanQrEvent {
    data object NavigateBack : ScanQrEvent
    data class NavigateToRecognizeStudent(val attendanceId: String) : ScanQrEvent
}
