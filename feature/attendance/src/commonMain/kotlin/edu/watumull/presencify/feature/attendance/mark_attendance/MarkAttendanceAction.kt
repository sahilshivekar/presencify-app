package edu.watumull.presencify.feature.attendance.mark_attendance

sealed interface MarkAttendanceAction {
    data object BackButtonClick : MarkAttendanceAction
    data object DynamicQRClick : MarkAttendanceAction
    data class ToggleStudentAttendance(val studentId: String, val currentStatus: Boolean) : MarkAttendanceAction
}
