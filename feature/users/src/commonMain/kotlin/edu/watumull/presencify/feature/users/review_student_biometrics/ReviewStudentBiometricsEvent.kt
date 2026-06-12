package edu.watumull.presencify.feature.users.review_student_biometrics

sealed interface ReviewStudentBiometricsEvent {
    data object NavigateBack : ReviewStudentBiometricsEvent
}
