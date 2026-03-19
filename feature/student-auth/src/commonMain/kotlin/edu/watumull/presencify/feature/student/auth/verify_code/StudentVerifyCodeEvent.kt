package edu.watumull.presencify.feature.student.auth.verify_code

sealed interface StudentVerifyCodeEvent {
    data object NavigateBack : StudentVerifyCodeEvent
    data object NavigateToHome : StudentVerifyCodeEvent
}
