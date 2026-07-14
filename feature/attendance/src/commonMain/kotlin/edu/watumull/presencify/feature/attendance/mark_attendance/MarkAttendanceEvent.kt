package edu.watumull.presencify.feature.attendance.mark_attendance

sealed interface MarkAttendanceEvent {
    data class NavigateBack(val attendanceId: String) : MarkAttendanceEvent
    data class NavigateToDynamicQR(val attendanceId: String) : MarkAttendanceEvent
}
