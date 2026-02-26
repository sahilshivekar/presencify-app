package edu.watumull.presencify.feature.schedule.timetable_details

sealed interface TimetableDetailsEvent {
    data object NavigateBack : TimetableDetailsEvent
    data class NavigateToEditTimetable(val timetableId: String) : TimetableDetailsEvent
    data class NavigateToAddClass(val timetableId: String) : TimetableDetailsEvent
    data class NavigateToClassDetails(val classId: String) : TimetableDetailsEvent
}
