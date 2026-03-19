package edu.watumull.presencify.feature.teacher.auth.forgot_password

sealed interface TeacherForgotPasswordAction {
    data class ChangeEmail(val email: String) : TeacherForgotPasswordAction
    data object ClickSendCode : TeacherForgotPasswordAction
    data object ClickBackButton : TeacherForgotPasswordAction
    data object DismissDialog : TeacherForgotPasswordAction
}
