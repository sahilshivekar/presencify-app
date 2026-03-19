package edu.watumull.presencify.feature.teacher.auth.forgot_password

sealed interface TeacherForgotPasswordEvent {
    data object NavigateBack : TeacherForgotPasswordEvent
    data class NavigateToVerifyCode(val email: String) : TeacherForgotPasswordEvent
}
