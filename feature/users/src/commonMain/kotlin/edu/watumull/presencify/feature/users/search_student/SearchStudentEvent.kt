package edu.watumull.presencify.feature.users.search_student

sealed interface SearchStudentEvent {
    data object NavigateBack : SearchStudentEvent
    data class NavigateToStudentDetails(val studentId: String) : SearchStudentEvent
    data object NavigateBackWithSelection : SearchStudentEvent

    data object NavigateToAddEditStudent : SearchStudentEvent
}

