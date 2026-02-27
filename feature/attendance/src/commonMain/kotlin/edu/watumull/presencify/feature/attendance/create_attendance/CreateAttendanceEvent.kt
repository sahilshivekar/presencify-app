package edu.watumull.presencify.feature.attendance.create_attendance

sealed interface CreateAttendanceEvent {
    data object NavigateBack : CreateAttendanceEvent
    data class NavigateToMarkAttendance(val attendanceId: String) : CreateAttendanceEvent
}
