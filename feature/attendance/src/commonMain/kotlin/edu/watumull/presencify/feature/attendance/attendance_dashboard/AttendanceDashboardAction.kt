package edu.watumull.presencify.feature.attendance.attendance_dashboard

sealed interface AttendanceDashboardAction {
    data object BackButtonClick : AttendanceDashboardAction
    data object NavigateToStudentAttendanceAnalytics : AttendanceDashboardAction
    data object NavigateToAggregateAttendanceAnalytics : AttendanceDashboardAction
    data object NavigateToSearchAttendance : AttendanceDashboardAction
    data object NavigateToCreateAttendance : AttendanceDashboardAction
}
