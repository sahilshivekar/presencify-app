package edu.watumull.presencify.feature.attendance.recognize_student

import edu.watumull.presencify.core.presentation.UiText


enum class HeadMovement {
    LEFT, RIGHT, STRAIGHT
}

data class RecognizeStudentState(
    val livenessSequence: List<HeadMovement> = emptyList(),
    val currentStep: Int = 0,
    val isLivenessComplete: Boolean = false,
    val isRecognizing: Boolean = false,
    val cameraPermissionGranted: Boolean = false,
    val error: UiText? = null,
    val isLoading: Boolean = false
)
