package edu.watumull.presencify.feature.attendance.create_attendance

import kotlinx.datetime.LocalDate

sealed interface CreateAttendanceAction {
    data object NavigateBack : CreateAttendanceAction
    data object DismissDialog : CreateAttendanceAction
    data class UpdateDate(val date: LocalDate?) : CreateAttendanceAction
    data object CreateAttendance : CreateAttendanceAction
}
