package edu.watumull.presencify.feature.schedule.class_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import kotlinx.coroutines.launch

class ClassDetailsViewModel(
    private val classSessionRepository: ClassSessionRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ClassDetailsState, ClassDetailsEvent, ClassDetailsAction>(
    initialState = ClassDetailsState(
        classId = savedStateHandle.toRoute<ScheduleRoutes.ClassDetails>().classId
    )
) {

    init {
        viewModelScope.launch {
            loadClass()
        }
    }

    private suspend fun loadClass() {
        val classId = state.classId

        classSessionRepository.getClassById(classId)
            .onSuccess { classSession ->
                updateState { it.copy(viewState = ClassDetailsState.ViewState.Content, classSession = classSession) }
            }
            .onError { error ->
                updateState { it.copy(viewState = ClassDetailsState.ViewState.Error(error.toUiText())) }
            }
    }

    override fun handleAction(action: ClassDetailsAction) {
        when (action) {
            is ClassDetailsAction.NavigateBack -> sendEvent(ClassDetailsEvent.NavigateBack)
            is ClassDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is ClassDetailsAction.RemoveClassClick -> handleRemoveClass()
            is ClassDetailsAction.ConfirmRemoveClass -> confirmRemoveClass()
            is ClassDetailsAction.EditClassClick -> {
                val timetableId = state.classSession?.timetableId
                if (timetableId != null) {
                    sendEvent(ClassDetailsEvent.NavigateToEditClass(timetableId, state.classId))
                }
            }

            ClassDetailsAction.MarkAttendanceClick -> {
                val classId = state.classSession?.id ?: return
                sendEvent(ClassDetailsEvent.NavigateToCreateAttendanceSheet(classId))
            }
        }
    }

    private fun handleRemoveClass() {
        updateState { it.copy(
            dialogState = DialogState(
                dialogType = DialogType.CONFIRM_RISKY_ACTION,
                title = UiText.DynamicString("Remove Class"),
                message = UiText.DynamicString("Are you sure you want to remove this class?")
            )
        ) }
    }

    private fun confirmRemoveClass() {
        updateState { it.copy(dialogState = null) }
        removeClass()
    }

    private fun removeClass() {
        viewModelScope.launch {
            val classId = state.classSession?.id ?: return@launch

            updateState { it.copy(isRemovingClass = true) }

            classSessionRepository.removeClass(classId)
                .onSuccess {
                    updateState { it.copy(isRemovingClass = false) }
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Class removed successfully")
                    )
                    sendEvent(ClassDetailsEvent.NavigateBack)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isRemovingClass = false,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Error Removing Class"),
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }
}
