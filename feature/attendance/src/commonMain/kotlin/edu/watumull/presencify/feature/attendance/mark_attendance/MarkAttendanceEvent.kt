package edu.watumull.presencify.feature.attendance.mark_attendance

sealed interface MarkAttendanceEvent {
    data object NavigateBack : MarkAttendanceEvent
}
