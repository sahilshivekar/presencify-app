package edu.watumull.presencify.feature.student.auth.forgot_password

sealed interface StudentForgotPasswordAction {
    data class ChangeEmail(val email: String) : StudentForgotPasswordAction
    data object ClickSendCode : StudentForgotPasswordAction
    data object ClickBackButton : StudentForgotPasswordAction
    data object DismissDialog : StudentForgotPasswordAction
}
