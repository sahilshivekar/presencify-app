package edu.watumull.presencify.feature.admin.mgt.admin_details

sealed interface AdminDetailsAction {
    data class ChangeEmail(val email: String) : AdminDetailsAction
    data class ChangeUsername(val username: String) : AdminDetailsAction
    data object ClickEditDetails : AdminDetailsAction
    data object ClickCancelEditingDetails : AdminDetailsAction
    data object ClickUpdateDetails : AdminDetailsAction
    data object ClickVerifyEmail : AdminDetailsAction
    data object ClickLogout : AdminDetailsAction
    data object ClickRemoveAccount : AdminDetailsAction
    data object ConfirmRemoveAccount : AdminDetailsAction
    data object ClickUpdatePassword : AdminDetailsAction
    data object ClickAddAdmin : AdminDetailsAction
    data object ClickBackButton : AdminDetailsAction
    data object DismissDialog : AdminDetailsAction
}
