package edu.watumull.presencify.feature.attendance.create_attendance

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.datetime.LocalDate

data class CreateAttendanceState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,

    // Class details
    val classSession: ClassSession? = null,

    // Date selection
    val selectedDate: LocalDate? = null,
    val dateError: String? = null,

    // Loading states
    val isCreatingAttendance: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val title: String = "",
        val message: UiText = UiText.DynamicString("")
    )
}
