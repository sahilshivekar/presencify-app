package edu.watumull.presencify.feature.users.student_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student.StudentDropoutRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.domain.repository.student_auth.StudentAuthRepository
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.users.navigation.UsersRoutes
import kotlinx.coroutines.launch

class StudentDetailsViewModel(
    private val studentRepository: StudentRepository,
    private val studentAuthRepository: StudentAuthRepository,
    private val studentDropoutRepository: StudentDropoutRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<StudentDetailsState, StudentDetailsEvent, StudentDetailsAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<UsersRoutes.StudentDetails>()
        StudentDetailsState(
            studentId = route.studentId,
            showSelfActions = route.showSelfActions
        )
    }
) {

    init {
        viewModelScope.launch {
            loadStudentDetails()
            loadDropoutDetails()
        }
    }

    private suspend fun loadStudentDetails() {
        val studentId = state.studentId

        studentRepository.getStudentById(studentId)
            .onSuccess { student ->
                updateState {
                    it.copy(
                        viewState = StudentDetailsState.ViewState.Content,
                        student = student,
                        studentSemesters = student.studentSemesters,
                        studentDivisions = student.studentDivisions,
                        studentBatches = student.studentBatches
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        viewState = StudentDetailsState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    private suspend fun loadDropoutDetails() {
        val studentId = state.studentId

        updateState { it.copy(areDropoutDetailsLoading = true) }

        studentDropoutRepository.getDropoutDetailsOfStudent(studentId)
            .onSuccess { dropouts ->
                updateState {
                    it.copy(
                        dropoutDetails = dropouts,
                        areDropoutDetailsLoading = false
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        areDropoutDetailsLoading = false,
                        dialogState = StudentDetailsState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Loading Dropout Details",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    override fun handleAction(action: StudentDetailsAction) {
        when (action) {
            is StudentDetailsAction.BackButtonClick -> {
                sendEvent(StudentDetailsEvent.NavigateBack)
            }

            is StudentDetailsAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is StudentDetailsAction.ToggleImageDialog -> {
                val newVisibility = !state.isImageDialogVisible
                updateState {
                    it.copy(
                        isImageDialogVisible = newVisibility,
                        newUploadedImageBytes = if (!newVisibility) null else it.newUploadedImageBytes
                    )
                }
            }

            is StudentDetailsAction.StudentNewImageUploaded -> {
                updateState {
                    it.copy(newUploadedImageBytes = action.imageBytes)
                }
            }

            is StudentDetailsAction.UpdateStudentImageClick -> {
                viewModelScope.launch {
                    updateStudentImage()
                }
            }

            is StudentDetailsAction.RemoveImageClick -> {
                viewModelScope.launch {
                    removeStudentImage()
                }
            }

            is StudentDetailsAction.RemoveStudentClick -> {
                updateState {
                    it.copy(
                        dialogState = StudentDetailsState.DialogState(
                            dialogType = DialogType.CONFIRM_RISKY_ACTION,
                            dialogIntention = DialogIntention.CONFIRM_REMOVE_STUDENT,
                            title = "Remove Student",
                            message = edu.watumull.presencify.core.presentation.UiText.DynamicString(
                                "Are you sure you want to remove ${state.student?.firstName} ${state.student?.lastName}?"
                            )
                        )
                    )
                }
            }

            is StudentDetailsAction.ConfirmRemoveStudent -> {
                viewModelScope.launch {
                    removeStudent()
                }
            }

            is StudentDetailsAction.EditStudentDetailsClick -> {
                sendEvent(StudentDetailsEvent.NavigateToEditStudent(state.studentId))
            }

            is StudentDetailsAction.LogoutClick -> {
                logoutStudent()
            }

            is StudentDetailsAction.ClickUpdatePassword -> {
                sendEvent(StudentDetailsEvent.NavigateToUpdatePassword)
            }
        }
    }

    private fun logoutStudent() {
        viewModelScope.launch {
                updateState { it.copy(isLoggingOut = true) }
                studentAuthRepository.logout()
                // Navigation or state cleanup is handled by AppViewModel observing userRole/userId
                updateState { it.copy(isLoggingOut = false) }

        }
    }

    private suspend fun updateStudentImage() {
        val imageBytes = state.newUploadedImageBytes
        val studentId = state.student?.id

        if (imageBytes == null || studentId == null) return

        updateState { it.copy(isUpdatingImage = true) }

        studentRepository.updateStudentImage(studentId, imageBytes)
            .onSuccess { updatedStudent ->
                updateState {
                    it.copy(
                        isUpdatingImage = false,
                        isImageDialogVisible = false,
                        newUploadedImageBytes = null,
                        student = updatedStudent
                    )
                }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Image updated successfully")
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isUpdatingImage = false,
                        isImageDialogVisible = false,
                        newUploadedImageBytes = null,
                        dialogState = StudentDetailsState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Updating Image",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun removeStudentImage() {
        val studentId = state.student?.id ?: return

        updateState { it.copy(isRemovingImage = true) }

        studentRepository.removeStudentImage(studentId)
            .onSuccess { updatedStudent ->
                updateState {
                    it.copy(
                        isRemovingImage = false,
                        isImageDialogVisible = false,
                        newUploadedImageBytes = null,
                        student = updatedStudent
                    )
                }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Image removed successfully")
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isRemovingImage = false,
                        isImageDialogVisible = false,
                        newUploadedImageBytes = null,
                        dialogState = StudentDetailsState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Removing Image",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }


    private suspend fun removeStudent() {
        val studentId = state.student?.id ?: return

        updateState {
            it.copy(
                isRemovingStudent = true,
                dialogState = null
            )
        }

        studentRepository.removeStudent(studentId)
            .onSuccess {
                updateState { it.copy(isRemovingStudent = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Student removed successfully")
                    )
                }
                sendEvent(StudentDetailsEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isRemovingStudent = false,
                        dialogState = StudentDetailsState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Removing Student",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
