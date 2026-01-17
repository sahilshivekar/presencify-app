package edu.watumull.presencify.feature.users.add_edit_teacher

sealed interface AddEditTeacherEvent {
    data object NavigateBack : AddEditTeacherEvent
}

