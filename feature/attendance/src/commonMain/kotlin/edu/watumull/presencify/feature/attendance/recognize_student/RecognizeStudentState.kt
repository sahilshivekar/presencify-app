package edu.watumull.presencify.feature.attendance.recognize_student

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
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
    val livenessSequence: List<HeadMovement> = emptyList(),
    val currentStep: Int = 0,
    val isLivenessComplete: Boolean = false,
    val isRecognizing: Boolean = false,
    val shouldCaptureEmbedding: Boolean = false,
    val cameraPermissionGranted: Boolean = false,
    val error: UiText? = null,
    val isLoading: Boolean = false,
    // New: flag suspicious behavior when face leaves camera during critical steps
    val isCheatingSuspected: Boolean = false,
    // Add a nullable dialogState property
    val dialogState: RecognizeStudentDialogState? = null,
    // Global screen-level timeout (90s) bookkeeping
    val isGlobalTimeoutActive: Boolean = false,
    val hasGlobalTimeoutFired: Boolean = false,
)
