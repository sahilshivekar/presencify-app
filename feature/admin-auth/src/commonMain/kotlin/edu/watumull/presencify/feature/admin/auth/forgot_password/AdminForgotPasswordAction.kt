package edu.watumull.presencify.feature.admin.auth.forgot_password

sealed interface AdminForgotPasswordAction {
    data class ChangeEmail(val email: String) : AdminForgotPasswordAction
    data object ClickSendCode : AdminForgotPasswordAction
    data object ClickBackButton : AdminForgotPasswordAction
    data object DismissDialog : AdminForgotPasswordAction
}
