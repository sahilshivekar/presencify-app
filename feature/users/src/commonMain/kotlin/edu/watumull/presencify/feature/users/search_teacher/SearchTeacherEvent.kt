package edu.watumull.presencify.feature.users.search_teacher

sealed interface SearchTeacherEvent {
    data object NavigateBack : SearchTeacherEvent
    data class NavigateToStaffDetails(val teacherId: String) : SearchTeacherEvent
    data object NavigateBackWithSelection : SearchTeacherEvent
    data object NavigateToAddEditStaff : SearchTeacherEvent
}

