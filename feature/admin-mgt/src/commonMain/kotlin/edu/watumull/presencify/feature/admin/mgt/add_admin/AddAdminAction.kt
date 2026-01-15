package edu.watumull.presencify.feature.admin.mgt.add_admin

sealed interface AddAdminAction {
    data class ChangeEmail(val email: String) : AddAdminAction
    data class ChangeUsername(val username: String) : AddAdminAction
    data class ChangePassword(val password: String) : AddAdminAction
    data class ChangeConfirmPassword(val confirmPassword: String) : AddAdminAction
    data object TogglePasswordVisibility : AddAdminAction
    data object ClickAddAdmin : AddAdminAction
    data object ClickBackButton : AddAdminAction
    data object DismissDialog : AddAdminAction
}
