package edu.watumull.presencify.feature.admin.mgt.admin_details

sealed interface AdminDetailsEvent {
    data object NavigateBack : AdminDetailsEvent
    data object NavigateToUpdatePassword : AdminDetailsEvent
    data class NavigateToVerifyCode(val email: String) : AdminDetailsEvent
    data object NavigateToAddAdmin : AdminDetailsEvent
}
