package edu.watumull.presencify.feature.attendance.attendance_details

import edu.watumull.presencify.core.domain.model.attendance.Attendance
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AttendanceDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val attendance: Attendance? = null,
    val classSession: ClassSession? = null,
    val totalStudents: Int = 0,
    val presentStudents: Int = 0,
    val absentStudents: Int = 0,
    val dialogState: DialogState? = null,
    val selectedTab: AttendanceTab = AttendanceTab.PRESENT
) {
    enum class AttendanceTab {
        PRESENT,
        ABSENT
    }
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }
}
