package edu.watumull.presencify.feature.admin.auth.verify_code

sealed interface AdminVerifyCodeEvent {
    data object NavigateBack : AdminVerifyCodeEvent
    data object NavigateToHome : AdminVerifyCodeEvent
}
