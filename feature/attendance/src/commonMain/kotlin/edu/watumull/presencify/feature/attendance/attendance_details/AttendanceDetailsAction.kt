package edu.watumull.presencify.feature.attendance.attendance_details

sealed interface AttendanceDetailsAction {
    data object BackButtonClick : AttendanceDetailsAction
    data object EditAttendanceClick : AttendanceDetailsAction
    data object RemoveAttendanceClick : AttendanceDetailsAction
    data object ConfirmRemoveAttendance : AttendanceDetailsAction
    data object DismissDialog : AttendanceDetailsAction
    data class TabClick(val tab: AttendanceDetailsState.AttendanceTab) : AttendanceDetailsAction
}
