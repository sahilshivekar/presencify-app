package edu.watumull.presencify.feature.users.student_details

import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus
import edu.watumull.presencify.core.domain.model.student.Dropout
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.domain.model.student.StudentBatch
import edu.watumull.presencify.core.domain.model.student.StudentDivision
import edu.watumull.presencify.core.domain.model.student.StudentSemester
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class StudentDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val studentId: String = "",
    val showSelfActions: Boolean = false,
    val student: Student? = null,
    val isImageDialogVisible: Boolean = false,
    val newUploadedImageBytes: ByteArray? = null,
    val isUpdatingImage: Boolean = false,
    val isRemovingImage: Boolean = false,
    val studentSemesters: List<StudentSemester>? = null,
    val studentDivisions: List<StudentDivision>? = null,
    val studentBatches: List<StudentBatch>? = null,
    val isRemovingStudent: Boolean = false,
    val isLoggingOut: Boolean = false,
    val dropoutDetails: List<Dropout> = emptyList(),
    val areDropoutDetailsLoading: Boolean = false,
    val biometricStatus: BiometricVerificationStatus? = null,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
