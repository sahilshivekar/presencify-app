package edu.watumull.presencify.feature.attendance.student_attendance_dashboard

sealed interface StudentAttendanceDashboardEvent {
    data object NavigateBack : StudentAttendanceDashboardEvent
    data class NavigateToSearchAttendanceForCourse(
        val courseId: String,
        val studentId: String,
    ) : StudentAttendanceDashboardEvent
}
