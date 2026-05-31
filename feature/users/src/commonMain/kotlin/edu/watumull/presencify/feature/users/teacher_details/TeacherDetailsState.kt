package edu.watumull.presencify.feature.users.teacher_details

import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.domain.model.teacher.Teacher
import edu.watumull.presencify.core.presentation.UiText

data class TeacherDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val teacherId: String = "",
    val showSelfActions: Boolean = false,
    val teacher: Teacher? = null,
    val isImageDialogVisible: Boolean = false,
    val newUploadedImageBytes: ByteArray? = null,
    val isUpdatingImage: Boolean = false,
    val isRemovingImage: Boolean = false,
    val isRemovingTeacher: Boolean = false,
    val isLoggingOut: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
