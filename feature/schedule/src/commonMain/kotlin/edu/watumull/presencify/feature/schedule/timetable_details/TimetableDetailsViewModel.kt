package edu.watumull.presencify.feature.schedule.timetable_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.domain.repository.schedule.TimetableRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import kotlinx.coroutines.launch

class TimetableDetailsViewModel(
    private val timetableRepository: TimetableRepository,
    private val classSessionRepository: ClassSessionRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TimetableDetailsState, TimetableDetailsEvent, TimetableDetailsAction>(
    initialState = TimetableDetailsState(
        timetableId = savedStateHandle.toRoute<ScheduleRoutes.TimetableDetails>().timetableId
    )
) {

    init {
        viewModelScope.launch {
            loadTimetable()
            loadClasses()
        }
    }

    private suspend fun loadTimetable() {
        val timetableId = state.timetableId

        timetableRepository.getTimetableById(timetableId)
            .onSuccess { timetable ->
                updateState { it.copy(viewState = TimetableDetailsState.ViewState.Content, timetable = timetable) }
            }
            .onError { error ->
                updateState { it.copy(viewState = TimetableDetailsState.ViewState.Error(error.toUiText())) }
            }
    }

    private suspend fun loadClasses() {
        val timetableId = state.timetableId
        updateState { it.copy(isLoadingClasses = true) }

        classSessionRepository.getClasses(
            timetableId = timetableId,
            getAll = true
        )
            .onSuccess { classListWithTotalCount ->
                // Group classes by day of week
                val classesByDay = classListWithTotalCount.classes.groupBy { it.dayOfWeek }
                updateState { it.copy(classesByDay = classesByDay, isLoadingClasses = false) }
            }
            .onError { error ->
                updateState { it.copy(isLoadingClasses = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Error loading classes")
                    )
                }
            }
    }

    override fun handleAction(action: TimetableDetailsAction) {
        when (action) {
            is TimetableDetailsAction.BackButtonClick -> sendEvent(TimetableDetailsEvent.NavigateBack)
            is TimetableDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is TimetableDetailsAction.DayTabClick -> updateState { it.copy(selectedDay = action.day) }
            is TimetableDetailsAction.ToggleShowInactiveClasses -> updateState { it.copy(showInactiveClasses = !it.showInactiveClasses) }
            is TimetableDetailsAction.AddClassClick -> sendEvent(TimetableDetailsEvent.NavigateToAddClass(state.timetableId))
            is TimetableDetailsAction.ClassClick -> sendEvent(TimetableDetailsEvent.NavigateToClassDetails(action.classId))
            is TimetableDetailsAction.RemoveTimetableClick -> updateState {
                it.copy(
                    dialogState = TimetableDetailsState.DialogState(
                        dialogType = DialogType.CONFIRM_RISKY_ACTION,
                        dialogIntention = DialogIntention.CONFIRM_REMOVE_TIMETABLE,
                        title = "Remove Timetable",
                        message = UiText.DynamicString(
                            "Are you sure you want to remove this timetable (Version ${state.timetable?.timetableVersion ?: "N/A"})?"
                        )
                    )
                )
            }
            is TimetableDetailsAction.ConfirmRemoveTimetable -> {
                viewModelScope.launch {
                    removeTimetable()
                }
            }
            is TimetableDetailsAction.EditTimetableClick -> sendEvent(TimetableDetailsEvent.NavigateToEditTimetable(state.timetableId))
        }
    }

    private suspend fun removeTimetable() {
        val timetableId = state.timetable?.id ?: return

        updateState { it.copy(isRemovingTimetable = true, dialogState = null) }

        timetableRepository.removeTimetable(timetableId)
            .onSuccess {
                updateState { it.copy(isRemovingTimetable = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Timetable removed successfully")
                    )
                }
                sendEvent(TimetableDetailsEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isRemovingTimetable = false,
                        dialogState = TimetableDetailsState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Removing Timetable",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
