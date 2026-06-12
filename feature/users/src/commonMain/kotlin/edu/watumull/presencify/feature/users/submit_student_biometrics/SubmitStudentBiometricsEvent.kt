package edu.watumull.presencify.feature.users.submit_student_biometrics

sealed interface SubmitStudentBiometricsEvent {
    data object NavigateBack : SubmitStudentBiometricsEvent
}
