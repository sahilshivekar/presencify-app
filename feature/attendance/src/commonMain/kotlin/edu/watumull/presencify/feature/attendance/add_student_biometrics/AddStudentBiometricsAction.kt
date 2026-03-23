package edu.watumull.presencify.feature.attendance.add_student_biometrics

sealed interface AddStudentBiometricsAction {
    data object NavigateBack : AddStudentBiometricsAction
    data class AddImages(val newImages: List<ByteArray>) : AddStudentBiometricsAction
    data class RemoveImage(val index: Int) : AddStudentBiometricsAction
    data object SubmitBiometrics : AddStudentBiometricsAction
    data object DismissDialog : AddStudentBiometricsAction
}
