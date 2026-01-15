package edu.watumull.presencify.feature.admin.auth.login

sealed interface AdminLoginAction {
    data class ChangeEmailOrUsername(val emailOrUsername: String) : AdminLoginAction
    data class ChangePassword(val password: String) : AdminLoginAction
    data class TogglePasswordVisibility(val isVisible: Boolean) : AdminLoginAction
    data object ClickLogin : AdminLoginAction
    data object ClickBackButton : AdminLoginAction
    data object ClickForgotPassword : AdminLoginAction
    data object DismissDialog : AdminLoginAction
}
