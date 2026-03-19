package edu.watumull.presencify.feature.student.auth.login

sealed interface StudentLoginAction {
    data class ChangeEmailOrPRN(val emailOrPRN: String) : StudentLoginAction
    data class ChangePassword(val password: String) : StudentLoginAction
    data class TogglePasswordVisibility(val isVisible: Boolean) : StudentLoginAction
    data object ClickLogin : StudentLoginAction
    data object ClickBackButton : StudentLoginAction
    data object ClickForgotPassword : StudentLoginAction
    data object DismissDialog : StudentLoginAction
}
