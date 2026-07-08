package edu.watumull.presencify.feature.schedule.class_details

sealed interface ClassDetailsEvent {
    data object NavigateBack : ClassDetailsEvent
    data class NavigateToEditClass(val timetableId: String, val classId: String) : ClassDetailsEvent
    data class NavigateToCreateAttendanceSheet(val classId: String) : ClassDetailsEvent
}
