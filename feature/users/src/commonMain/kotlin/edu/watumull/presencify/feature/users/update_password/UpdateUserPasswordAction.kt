package edu.watumull.presencify.feature.users.update_password

sealed interface UpdateUserPasswordAction {
    data class ChangePassword(val password: String) : UpdateUserPasswordAction
    data class ChangeConfirmPassword(val confirmPassword: String) : UpdateUserPasswordAction
    data object TogglePasswordVisibility : UpdateUserPasswordAction
    data object ClickUpdatePassword : UpdateUserPasswordAction
    data object ClickBackButton : UpdateUserPasswordAction
    data object DismissDialog : UpdateUserPasswordAction
}
