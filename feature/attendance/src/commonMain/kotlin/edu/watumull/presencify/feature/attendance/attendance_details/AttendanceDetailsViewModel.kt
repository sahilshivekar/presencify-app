package edu.watumull.presencify.feature.attendance.attendance_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
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
            AttendanceDetailsAction.BackButtonClick -> sendEvent(AttendanceDetailsEvent.NavigateBack)
            AttendanceDetailsAction.EditAttendanceClick -> sendEvent(
                AttendanceDetailsEvent.NavigateToEditAttendance(attendanceId)
            )
            AttendanceDetailsAction.RemoveAttendanceClick -> showRemoveConfirmation()
            AttendanceDetailsAction.ConfirmRemoveAttendance -> removeAttendance()
            AttendanceDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is AttendanceDetailsAction.TabClick -> updateState { it.copy(selectedTab = action.tab) }
        }
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

    private fun showRemoveConfirmation() {
        updateState { it.copy(
            dialogState = AttendanceDetailsState.DialogState(
                message = UiText.DynamicString("Are you sure you want to remove this attendance record? This action cannot be undone."),
                onConfirm = {
                    updateState { it.copy(dialogState = null) }
                    trySendAction(AttendanceDetailsAction.ConfirmRemoveAttendance)
                },
                onDismiss = {
                    updateState { it.copy(dialogState = null) }
                }
            )
        ) }
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
                        dialogState = AttendanceDetailsState.DialogState(
                            message = error.toUiText(),
                            onConfirm = {
                                updateState { it.copy(dialogState = null) }
                            },
                            onDismiss = {
                                updateState { it.copy(dialogState = null) }
                            }
                        )
                    ) }
                }
        }
    }
}
