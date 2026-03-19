package edu.watumull.presencify.feature.teacher.auth.verify_code

sealed interface TeacherVerifyCodeEvent {
    data object NavigateBack : TeacherVerifyCodeEvent
    data object NavigateToHome : TeacherVerifyCodeEvent
}
