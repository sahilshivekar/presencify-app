package edu.watumull.presencify.feature.users.submit_student_biometrics

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.launch

class SubmitStudentBiometricsViewModel(
    private val studentRepository: StudentRepository,
) : BaseViewModel<SubmitStudentBiometricsState, SubmitStudentBiometricsEvent, SubmitStudentBiometricsAction>(
    initialState = SubmitStudentBiometricsState()
) {
    override fun handleAction(action: SubmitStudentBiometricsAction) {
        when (action) {
            SubmitStudentBiometricsAction.NavigateBack -> sendEvent(SubmitStudentBiometricsEvent.NavigateBack)
            is SubmitStudentBiometricsAction.AddImages -> addImages(action.newImages)
            is SubmitStudentBiometricsAction.RemoveImage -> removeImage(action.index)
            SubmitStudentBiometricsAction.SubmitBiometrics -> submitBiometrics()
            SubmitStudentBiometricsAction.DismissDialog -> dismissDialog()
        }
    }

    private fun addImages(newImages: List<ByteArray>) {
        val currentImages = state.images
        if (currentImages.size + newImages.size > 10) {
            updateState {
                it.copy(
                    dialogState = DialogState(
                        title = UiText.DynamicString("Limit Reached"),
                        message = UiText.DynamicString("You can select a maximum of 10 images."),
                        dialogType = DialogType.ERROR
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

            val descriptors = extractFaceDescriptors(state.images)

            if (descriptors.isNullOrEmpty()) {
                updateState {
                    it.copy(
                        isLoading = false,
                        dialogState = DialogState(
                            title = UiText.DynamicString("No Faces Detected"),
                            message = UiText.DynamicString("No faces detected in selected images."),
                            dialogType = DialogType.ERROR
                        )
                    )
                }
                return@launch
            }

            val centroid = calculateCentroid(descriptors)
            val centroidList: List<Float> = centroid.toList()

            val result = studentRepository.submitBiometrics(
                images = state.images,
                faceDescriptor = centroidList
            )

            updateState { it.copy(isLoading = false) }

            result.onSuccess {
                SnackbarController.sendEvent(SnackbarEvent("Biometrics submitted successfully"))
                sendEvent(SubmitStudentBiometricsEvent.NavigateBack)
            }.onError { error ->
                updateState {
                    it.copy(
                        dialogState = DialogState(
                            title = UiText.DynamicString("Error"),
                            message = error.toUiText(),
                            dialogType = DialogType.ERROR
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
