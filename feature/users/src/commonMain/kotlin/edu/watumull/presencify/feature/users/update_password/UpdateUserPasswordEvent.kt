package edu.watumull.presencify.feature.users.update_password

sealed interface UpdateUserPasswordEvent {
    data object NavigateBack : UpdateUserPasswordEvent
    data object NavigateToMyDetails : UpdateUserPasswordEvent
}
