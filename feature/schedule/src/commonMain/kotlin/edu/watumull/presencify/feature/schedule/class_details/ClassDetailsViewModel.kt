package edu.watumull.presencify.feature.schedule.class_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.presentation.UiText
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
            is ClassDetailsAction.BackButtonClick -> sendEvent(ClassDetailsEvent.NavigateBack)
            is ClassDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is ClassDetailsAction.RemoveClassClick -> updateState {
                it.copy(
                    dialogState = ClassDetailsState.DialogState(
                        dialogType = DialogType.CONFIRM_RISKY_ACTION,
                        dialogIntention = DialogIntention.CONFIRM_REMOVE_CLASS,
                        title = "Remove Class",
                        message = UiText.DynamicString(
                            "Are you sure you want to remove this class?"
                        )
                    )
                )
            }
            is ClassDetailsAction.ConfirmRemoveClass -> {
                viewModelScope.launch {
                    removeClass()
                }
            }
            is ClassDetailsAction.EditClassClick -> {
                val timetableId = state.classSession?.timetableId
                if (timetableId != null) {
                    sendEvent(ClassDetailsEvent.NavigateToEditClass(timetableId, state.classId))
                }
            }
        }
    }

    private suspend fun removeClass() {
        val classId = state.classSession?.id ?: return

        updateState { it.copy(isRemovingClass = true, dialogState = null) }

        classSessionRepository.removeClass(classId)
            .onSuccess {
                updateState { it.copy(isRemovingClass = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Class removed successfully")
                    )
                }
                sendEvent(ClassDetailsEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isRemovingClass = false,
                        dialogState = ClassDetailsState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Removing Class",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
