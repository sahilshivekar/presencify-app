package edu.watumull.presencify.feature.attendance.student_analytics

sealed interface StudentAttendanceAnalyticsEvent {
    data object NavigateBack : StudentAttendanceAnalyticsEvent
}
