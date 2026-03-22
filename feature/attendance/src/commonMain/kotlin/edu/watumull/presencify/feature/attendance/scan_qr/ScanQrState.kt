package edu.watumull.presencify.feature.attendance.scan_qr

data class ScanQrState(
    val isLoading: Boolean = false,
    val lastScannedContent: String? = null
)
