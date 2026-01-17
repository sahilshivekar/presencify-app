package edu.watumull.presencify.feature.users.add_edit_student

sealed interface AddEditStudentEvent {
    data object NavigateBack : AddEditStudentEvent
}

