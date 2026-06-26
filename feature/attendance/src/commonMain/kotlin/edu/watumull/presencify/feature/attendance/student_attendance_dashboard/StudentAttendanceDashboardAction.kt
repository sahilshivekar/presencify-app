package edu.watumull.presencify.feature.attendance.student_attendance_dashboard

sealed interface StudentAttendanceDashboardAction {
    data object NavigateBack : StudentAttendanceDashboardAction
    data object DismissDialog : StudentAttendanceDashboardAction
    data object ScanQrClick : StudentAttendanceDashboardAction
    data class SelectSemester(val semesterId: String) : StudentAttendanceDashboardAction
    data class ToggleCourseSelection(val courseId: String) : StudentAttendanceDashboardAction
    data class DonutCourseClick(val courseId: String) : StudentAttendanceDashboardAction
}
