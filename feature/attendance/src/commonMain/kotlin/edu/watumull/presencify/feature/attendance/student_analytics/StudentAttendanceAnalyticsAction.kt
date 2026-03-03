package edu.watumull.presencify.feature.attendance.student_analytics

sealed interface StudentAttendanceAnalyticsAction {
    data object BackButtonClick : StudentAttendanceAnalyticsAction
    data object DismissDialog : StudentAttendanceAnalyticsAction
    data class ToggleSemesterExpansion(val semesterId: String) : StudentAttendanceAnalyticsAction
}
