package edu.watumull.presencify.feature.attendance.add_student_biometrics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.launch

class AddStudentBiometricsViewModel(
    private val studentRepository: StudentRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddStudentBiometricsState, AddStudentBiometricsEvent, AddStudentBiometricsAction>(
    initialState = AddStudentBiometricsState(
        studentId = savedStateHandle.toRoute<AttendanceRoutes.AddStudentBiometrics>().studentId
    )
) {
    override fun handleAction(action: AddStudentBiometricsAction) {
        when (action) {
            AddStudentBiometricsAction.NavigateBack -> sendEvent(AddStudentBiometricsEvent.NavigateBack)
            is AddStudentBiometricsAction.AddImages -> addImages(action.newImages)
            is AddStudentBiometricsAction.RemoveImage -> removeImage(action.index)
            AddStudentBiometricsAction.SubmitBiometrics -> submitBiometrics()
            AddStudentBiometricsAction.DismissDialog -> dismissDialog()
        }
    }

    private fun addImages(newImages: List<ByteArray>) {
        val currentImages = state.images
        if (currentImages.size + newImages.size > 10) {
            updateState {
                it.copy(
                    dialogState = AddStudentBiometricsState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Limit Reached",
                        message = UiText.DynamicString("You can select a maximum of 10 images.")
                    )
                )
            }
            return
        }
        updateState { it.copy(images = currentImages + newImages) }
    }

    private fun removeImage(index: Int) {
        val currentImages = state.images.toMutableList()
        currentImages.removeAt(index)
        updateState { it.copy(images = currentImages) }
    }

    private fun submitBiometrics() {
        if (state.images.isEmpty()) {
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }

            val result = studentRepository.enrollStudentFace(
                studentId = state.studentId,
                images = state.images,
            )
            updateState { it.copy(isLoading = false) }

            result.onSuccess {
                SnackbarController.sendEvent(SnackbarEvent("Biometrics added successfully"))
                sendEvent(AddStudentBiometricsEvent.NavigateBack)
            }.onError { error ->
                updateState {
                    it.copy(
                        dialogState = AddStudentBiometricsState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error",
                            message = error.toUiText()
                        )
                    )
                }
            }
        }
    }

    private fun dismissDialog() {
        updateState { it.copy(dialogState = null) }
    }

    private fun calculateCentroid(embeddings: List<FloatArray>): FloatArray {
        if (embeddings.isEmpty()) return FloatArray(0)

        val dimension = embeddings[0].size
        val sum = FloatArray(dimension)

        for (embedding in embeddings) {
            for (i in 0 until dimension) {
                sum[i] += embedding[i]
            }
        }

        val count = embeddings.size
        return FloatArray(dimension) { i -> sum[i] / count }
    }
}
