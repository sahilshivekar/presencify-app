package edu.watumull.presencify.feature.attendance.create_attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.create_attendance.CreateAttendanceEvent.NavigateBack
import edu.watumull.presencify.feature.attendance.create_attendance.CreateAttendanceEvent.NavigateToMarkAttendance
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.launch

class CreateAttendanceViewModel(
    private val classSessionRepository: ClassSessionRepository,
    private val attendanceRepository: AttendanceRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CreateAttendanceState, CreateAttendanceEvent, CreateAttendanceAction>(
    initialState = CreateAttendanceState()
) {

    private val classId: String = savedStateHandle.toRoute<AttendanceRoutes.CreateAttendanceSheet>().classId

    init {
        loadClassDetails()
    }

    private fun loadClassDetails() {
        viewModelScope.launch {
            updateState { it.copy(viewState = CreateAttendanceState.ViewState.Loading) }

            classSessionRepository.getClassById(classId)
                .onSuccess { classSession ->
                    updateState {
                        it.copy(
                            viewState = CreateAttendanceState.ViewState.Content,
                            classSession = classSession
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            viewState = CreateAttendanceState.ViewState.Error(error.toUiText()),
                            dialogState = CreateAttendanceState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }

    private fun createAttendance() {
        val state = stateFlow.value

        // Validate date
        if (state.selectedDate == null) {
            updateState {
                it.copy(
                    dateError = "Please select a date",
                )
            }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isCreatingAttendance = true) }

            attendanceRepository.createAttendance(
                classId = classId,
                date = state.selectedDate
            )
                .onSuccess { attendance ->
                    updateState {
                        it.copy(
                            isCreatingAttendance = false,
                        )
                    }
                    // Navigate to mark attendance screen
                    sendEvent(NavigateToMarkAttendance(attendance.id))
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isCreatingAttendance = false,
                            dialogState = CreateAttendanceState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: CreateAttendanceAction) {
        when (action) {
            CreateAttendanceAction.BackButtonClick -> {
                sendEvent(NavigateBack)
            }

            CreateAttendanceAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is CreateAttendanceAction.UpdateDate -> {
                updateState {
                    it.copy(
                        selectedDate = action.date,
                        dateError = null
                    )
                }
            }

            CreateAttendanceAction.CreateAttendance -> {
                createAttendance()
            }
        }
    }
}
