package edu.watumull.presencify.feature.attendance.student_analytics

sealed interface StudentAttendanceAnalyticsAction {
    data object NavigateBack : StudentAttendanceAnalyticsAction
    data object DismissDialog : StudentAttendanceAnalyticsAction
    data class ToggleSemesterExpansion(val semesterId: String) : StudentAttendanceAnalyticsAction
    data class DonutCourseClick(val courseId: String) : StudentAttendanceAnalyticsAction
    data object ScanQrClick : StudentAttendanceAnalyticsAction
}
