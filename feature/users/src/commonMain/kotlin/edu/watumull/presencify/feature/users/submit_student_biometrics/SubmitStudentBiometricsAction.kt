package edu.watumull.presencify.feature.users.submit_student_biometrics

sealed interface SubmitStudentBiometricsAction {
    data object NavigateBack : SubmitStudentBiometricsAction
    data class AddImages(val newImages: List<ByteArray>) : SubmitStudentBiometricsAction
    data class RemoveImage(val index: Int) : SubmitStudentBiometricsAction
    data object SubmitBiometrics : SubmitStudentBiometricsAction
    data object DismissDialog : SubmitStudentBiometricsAction
}
