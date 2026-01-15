package edu.watumull.presencify.feature.admin.auth.verify_code

sealed interface AdminVerifyCodeAction {
    data class ChangeCode(val code: String) : AdminVerifyCodeAction
    data object ClickVerifyCode : AdminVerifyCodeAction
    data object ClickBackButton : AdminVerifyCodeAction
    data object DismissDialog : AdminVerifyCodeAction
}
