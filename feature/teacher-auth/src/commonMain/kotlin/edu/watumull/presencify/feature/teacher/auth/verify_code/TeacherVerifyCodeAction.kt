package edu.watumull.presencify.feature.teacher.auth.verify_code

sealed interface TeacherVerifyCodeAction {
    data class ChangeCode(val code: String) : TeacherVerifyCodeAction
    data object ClickVerifyCode : TeacherVerifyCodeAction
    data object ClickBackButton : TeacherVerifyCodeAction
    data object DismissDialog : TeacherVerifyCodeAction
}
