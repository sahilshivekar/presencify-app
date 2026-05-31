package edu.watumull.presencify.feature.schedule.timetable_details

import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.domain.model.schedule.Timetable
import edu.watumull.presencify.core.presentation.UiText

data class TimetableDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val timetableId: String = "",
    val timetable: Timetable? = null,
    val isRemovingTimetable: Boolean = false,
    val classesByDay: Map<DayOfWeek, List<ClassSession>> = emptyMap(),
    val isLoadingClasses: Boolean = false,
    val selectedDay: DayOfWeek = DayOfWeek.MONDAY,
    val showInactiveClasses: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
