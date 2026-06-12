package edu.watumull.presencify.feature.users.review_student_biometrics

sealed interface ReviewStudentBiometricsAction {
    data object NavigateBack : ReviewStudentBiometricsAction
    data object ApproveStudentBiometrics : ReviewStudentBiometricsAction
    data object DismissDialog : ReviewStudentBiometricsAction
    data object RetryLoad : ReviewStudentBiometricsAction
}
