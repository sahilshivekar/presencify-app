package edu.watumull.presencify.feature.admin.auth.forgot_password

sealed interface AdminForgotPasswordEvent {
    data object NavigateBack : AdminForgotPasswordEvent
    data class NavigateToVerifyCode(val email: String) : AdminForgotPasswordEvent
}
