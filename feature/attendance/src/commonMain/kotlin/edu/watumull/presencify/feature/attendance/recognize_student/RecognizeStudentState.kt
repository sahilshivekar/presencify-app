package edu.watumull.presencify.feature.attendance.recognize_student

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState


enum class HeadMovement {
    LEFT, RIGHT, STRAIGHT
}

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
    val dialogState: DialogState? = null,
    val isGlobalTimeoutActive: Boolean = false,
    val hasGlobalTimeoutFired: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }
}
