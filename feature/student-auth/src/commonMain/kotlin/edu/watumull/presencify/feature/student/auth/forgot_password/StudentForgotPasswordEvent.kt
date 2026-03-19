package edu.watumull.presencify.feature.student.auth.forgot_password

sealed interface StudentForgotPasswordEvent {
    data object NavigateBack : StudentForgotPasswordEvent
    data class NavigateToVerifyCode(val email: String) : StudentForgotPasswordEvent
}
