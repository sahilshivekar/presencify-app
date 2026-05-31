package edu.watumull.presencify.feature.attendance.attendance_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.utils.ShareUtils
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.launch

class AttendanceDetailsViewModel(
    private val attendanceRepository: AttendanceRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AttendanceDetailsState, AttendanceDetailsEvent, AttendanceDetailsAction>(
    initialState = AttendanceDetailsState()
) {
    private val attendanceId: String = savedStateHandle.toRoute<AttendanceRoutes.AttendanceDetails>().attendanceId

    init {
        loadAttendanceDetails()
    }

    override fun handleAction(action: AttendanceDetailsAction) {
        when (action) {
            AttendanceDetailsAction.NavigateBack -> sendEvent(AttendanceDetailsEvent.NavigateBack)
            AttendanceDetailsAction.EditAttendanceClick -> sendEvent(
                AttendanceDetailsEvent.NavigateToEditAttendance(attendanceId)
            )
            AttendanceDetailsAction.RemoveAttendanceClick -> handleRemoveAttendance()
            AttendanceDetailsAction.ConfirmRemoveAttendance -> confirmRemoveAttendance()
            AttendanceDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is AttendanceDetailsAction.TabClick -> updateState { it.copy(selectedTab = action.tab) }
            is AttendanceDetailsAction.ShareAttendance -> shareAttendanceDetails(action.text)
        }
    }

    private fun handleRemoveAttendance() {
        updateState { it.copy(
            dialogState = DialogState(
                dialogType = DialogType.CONFIRM_RISKY_ACTION,
                title = UiText.DynamicString("Remove Attendance"),
                message = UiText.DynamicString("Are you sure you want to remove this attendance record? This action cannot be undone.")
            )
        ) }
    }

    private fun confirmRemoveAttendance() {
        updateState { it.copy(dialogState = null) }
        removeAttendance()
    }

    private fun loadAttendanceDetails() {
        viewModelScope.launch {
            attendanceRepository.getAttendanceById(attendanceId)
                .onSuccess { attendance ->
                    val attendanceStudents = attendance.attendanceStudents ?: emptyList()
                    val presentCount = attendanceStudents.count { it.attendanceStatus }
                    val totalCount = attendanceStudents.size
                    val absentCount = totalCount - presentCount

                    updateState { it.copy(
                        viewState = AttendanceDetailsState.ViewState.Content,
                        attendance = attendance,
                        classSession = attendance.klass,
                        totalStudents = totalCount,
                        presentStudents = presentCount,
                        absentStudents = absentCount
                    ) }
                }
                .onError { error ->
                    updateState { it.copy(
                        viewState = AttendanceDetailsState.ViewState.Error(error.toUiText())
                    ) }
                }
        }
    }

    private fun removeAttendance() {
        viewModelScope.launch {
            attendanceRepository.removeAttendance(attendanceId)
                .onSuccess {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Attendance removed successfully")
                    )
                    sendEvent(AttendanceDetailsEvent.NavigateBack)
                }
                .onError { error ->
                    updateState { it.copy(
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error"),
                            message = error.toUiText()
                        )
                    ) }
                }
        }
    }

    private fun shareAttendanceDetails(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            ShareUtils.shareText(text)
        }
    }
}
