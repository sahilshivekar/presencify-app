package edu.watumull.presencify.feature.attendance.search_attendance

sealed interface SearchAttendanceEvent {
    data object NavigateBack : SearchAttendanceEvent
    data class NavigateToAttendanceDetails(val attendanceId: String) : SearchAttendanceEvent
}
