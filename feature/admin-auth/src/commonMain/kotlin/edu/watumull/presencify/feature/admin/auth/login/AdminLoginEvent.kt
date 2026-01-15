package edu.watumull.presencify.feature.admin.auth.login

sealed interface AdminLoginEvent {
    data object NavigateBack : AdminLoginEvent
    data object NavigateToHome : AdminLoginEvent
    data object NavigateToForgotPassword : AdminLoginEvent
}
