package edu.watumull.presencify.feature.teacher.auth.login

sealed interface TeacherLoginAction {
    data class ChangeEmail(val email: String) : TeacherLoginAction
    data class ChangePassword(val password: String) : TeacherLoginAction
    data class TogglePasswordVisibility(val isVisible: Boolean) : TeacherLoginAction
    data object ClickLogin : TeacherLoginAction
    data object ClickBackButton : TeacherLoginAction
    data object ClickForgotPassword : TeacherLoginAction
    data object DismissDialog : TeacherLoginAction
}
