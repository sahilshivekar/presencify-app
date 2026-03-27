package edu.watumull.presencify.feature.attendance.mark_attendance

sealed interface MarkAttendanceEvent {
    data object NavigateBack : MarkAttendanceEvent
    data class NavigateToDynamicQR(val attendanceId: String) : MarkAttendanceEvent
    data class NavigateToGroupPhotoScan(val attendanceId: String) : MarkAttendanceEvent
}
