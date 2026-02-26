package edu.watumull.presencify.feature.schedule.add_edit_timetable

sealed interface AddEditTimetableEvent {
    data object NavigateBack : AddEditTimetableEvent
    data class NavigateToTimetableDetails(val timetableId: String) : AddEditTimetableEvent
}
