package edu.watumull.presencify.feature.attendance.create_attendance

import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils.getCurrentDate
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import kotlinx.datetime.LocalDate

data class CreateAttendanceState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,

    val classSession: ClassSession? = null,

    val selectedDate: LocalDate? = getCurrentDate(),
    val dateError: String? = null,

    val isCreatingAttendance: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
