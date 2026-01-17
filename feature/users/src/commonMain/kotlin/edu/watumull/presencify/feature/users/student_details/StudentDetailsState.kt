package edu.watumull.presencify.feature.users.student_details

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.model.student.*
import edu.watumull.presencify.core.presentation.UiText

data class StudentDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val studentId: String = "",
    val student: Student? = null,
    val isImageDialogVisible: Boolean = false,
    val newUploadedImageBytes: ByteArray? = null,
    val isUpdatingImage: Boolean = false,
    val isRemovingImage: Boolean = false,
    val studentSemesters: List<StudentSemester>? = null,
    val studentDivisions: List<StudentDivision>? = null,
    val studentBatches: List<StudentBatch>? = null,
    val isRemovingStudent: Boolean = false,
    val dropoutDetails: List<Dropout> = emptyList(),
    val areDropoutDetailsLoading: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC,
        val title: String = "",
        val message: UiText? = null,
    )
}

enum class DialogIntention {
    GENERIC,
    CONFIRM_REMOVE_STUDENT,
}

