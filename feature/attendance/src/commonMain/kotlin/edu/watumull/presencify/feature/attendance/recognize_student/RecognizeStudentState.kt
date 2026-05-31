package edu.watumull.presencify.feature.attendance.recognize_student

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText


enum class HeadMovement {
    LEFT, RIGHT, STRAIGHT
}

data class RecognizeStudentDialogState(
    val isVisible: Boolean = false,
    val dialogType: DialogType = DialogType.ERROR,
    val title: String,
    val message: UiText,
)

data class RecognizeStudentState(
    val viewState: ViewState = ViewState.Loading,
    val livenessSequence: List<HeadMovement> = emptyList(),
    val currentStep: Int = 0,
    val isLivenessComplete: Boolean = false,
    val isRecognizing: Boolean = false,
    val shouldCaptureEmbedding: Boolean = false,
    val cameraPermissionGranted: Boolean = false,
    val error: UiText? = null,
    val isLoading: Boolean = false,
    val isCheatingSuspected: Boolean = false,
    val dialogState: RecognizeStudentDialogState? = null,
    val isGlobalTimeoutActive: Boolean = false,
    val hasGlobalTimeoutFired: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }
}
