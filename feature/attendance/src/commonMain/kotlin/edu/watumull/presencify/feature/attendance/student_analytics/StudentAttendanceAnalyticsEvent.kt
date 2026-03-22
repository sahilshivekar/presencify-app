package edu.watumull.presencify.feature.attendance.student_analytics

sealed interface StudentAttendanceAnalyticsEvent {
    data object NavigateBack : StudentAttendanceAnalyticsEvent
    data class NavigateToSearchAttendanceForCourse(
        val courseId: String,
        val studentId: String,
    ) : StudentAttendanceAnalyticsEvent
    data object NavigateToScanQr : StudentAttendanceAnalyticsEvent
}
