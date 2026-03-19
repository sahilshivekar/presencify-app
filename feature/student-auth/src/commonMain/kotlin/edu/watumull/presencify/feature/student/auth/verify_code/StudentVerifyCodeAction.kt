package edu.watumull.presencify.feature.student.auth.verify_code

sealed interface StudentVerifyCodeAction {
    data class ChangeCode(val code: String) : StudentVerifyCodeAction
    data object ClickVerifyCode : StudentVerifyCodeAction
    data object ClickBackButton : StudentVerifyCodeAction
    data object DismissDialog : StudentVerifyCodeAction
}
