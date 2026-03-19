package edu.watumull.presencify.feature.student.auth.login

sealed interface StudentLoginEvent {
    data object NavigateBack : StudentLoginEvent
    data object NavigateToHome : StudentLoginEvent
    data object NavigateToForgotPassword : StudentLoginEvent
}
