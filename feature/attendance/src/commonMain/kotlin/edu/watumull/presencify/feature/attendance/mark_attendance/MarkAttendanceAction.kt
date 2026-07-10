package edu.watumull.presencify.feature.attendance.mark_attendance

sealed interface MarkAttendanceAction {
    data object NavigateBack : MarkAttendanceAction
    data object DynamicQRClick : MarkAttendanceAction
    data object ShareAttendanceSummary : MarkAttendanceAction
    data class ToggleStudentAttendance(val studentId: String, val currentStatus: Boolean) : MarkAttendanceAction
    data object ClickMarkAllPresent : MarkAttendanceAction
    data object ClickMarkAllAbsent : MarkAttendanceAction
    data object DismissDialog : MarkAttendanceAction
}
