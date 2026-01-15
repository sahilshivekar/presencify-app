package edu.watumull.presencify.feature.admin.mgt.update_password

sealed interface UpdatePasswordEvent {
    data object NavigateBack : UpdatePasswordEvent
    data object NavigateToAdminDetails : UpdatePasswordEvent
}
