package edu.watumull.presencify.feature.teacher.auth.login

sealed interface TeacherLoginEvent {
    data object NavigateBack : TeacherLoginEvent
    data object NavigateToHome : TeacherLoginEvent
    data object NavigateToForgotPassword : TeacherLoginEvent
}
