package edu.watumull.presencify.feature.attendance.mark_attendance

import edu.watumull.presencify.core.domain.model.attendance.Attendance
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.ListItemFeedback
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class MarkAttendanceState(
    val viewState: ViewState = ViewState.Loading,
    val attendance: Attendance? = null,
    val classSession: ClassSession? = null,
    val totalStudents: Int = 0,
    val presentStudents: Int = 0,
    val absentStudents: Int = 0,
    val studentLoadingStates: Map<String, Boolean> = emptyMap(),
    val studentFeedbacks: Map<String, ListItemFeedback> = emptyMap(),
    val isMarkingAllPresent: Boolean = false,
    val isMarkingAllAbsent: Boolean = false,
    val dialogState: DialogState? = null,
    val qrCodeContent: String = "",
    val isQrVisible: Boolean = false,
    val isStudentListLoading: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }
}
