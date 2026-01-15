package edu.watumull.presencify.feature.admin.mgt.update_password

sealed interface UpdatePasswordAction {
    data class ChangePassword(val password: String) : UpdatePasswordAction
    data class ChangeConfirmPassword(val confirmPassword: String) : UpdatePasswordAction
    data object TogglePasswordVisibility : UpdatePasswordAction
    data object ClickUpdatePassword : UpdatePasswordAction
    data object ClickBackButton : UpdatePasswordAction
    data object DismissDialog : UpdatePasswordAction
}
