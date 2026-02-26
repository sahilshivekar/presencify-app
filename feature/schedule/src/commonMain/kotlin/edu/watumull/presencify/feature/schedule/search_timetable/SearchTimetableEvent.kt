package edu.watumull.presencify.feature.schedule.search_timetable

sealed interface SearchTimetableEvent {
    data object NavigateBack : SearchTimetableEvent
    data class NavigateToTimetableDetails(val timetableId: String) : SearchTimetableEvent
    data object NavigateToAddTimetable : SearchTimetableEvent
}
