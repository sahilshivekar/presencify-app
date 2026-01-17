package edu.watumull.presencify.feature.users.student_details

sealed interface StudentDetailsEvent {
    data object NavigateBack : StudentDetailsEvent
    data class NavigateToEditStudent(val studentId: String) : StudentDetailsEvent
}

