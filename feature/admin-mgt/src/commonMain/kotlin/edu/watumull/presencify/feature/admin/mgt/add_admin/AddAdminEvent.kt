package edu.watumull.presencify.feature.admin.mgt.add_admin

sealed interface AddAdminEvent {
    data object NavigateBack : AddAdminEvent
    data object NavigateToAdminDetails : AddAdminEvent
}
