package edu.watumull.presencify.feature.attendance.attendance_details

sealed interface AttendanceDetailsEvent {
    data object NavigateBack : AttendanceDetailsEvent
    data class NavigateToEditAttendance(val attendanceId: String) : AttendanceDetailsEvent
}
