package edu.watumull.presencify.feature.attendance.attendance_dashboard

sealed interface AttendanceDashboardEvent {
    data object NavigateBack : AttendanceDashboardEvent
    data object NavigateToStudentAttendanceAnalytics : AttendanceDashboardEvent
    data object NavigateToAggregateAttendanceAnalytics : AttendanceDashboardEvent
    data object NavigateToSearchAttendance : AttendanceDashboardEvent
    data object NavigateToCreateAttendance : AttendanceDashboardEvent
    data object NavigateToSearchStudentForBiometrics : AttendanceDashboardEvent
    data object NavigateToDefaulters : AttendanceDashboardEvent
}
