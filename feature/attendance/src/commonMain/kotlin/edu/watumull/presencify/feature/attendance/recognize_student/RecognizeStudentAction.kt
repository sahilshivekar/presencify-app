package edu.watumull.presencify.feature.attendance.recognize_student

import edu.watumull.presencify.core.presentation.UiText


sealed interface RecognizeStudentAction {
    data object NavigateBack : RecognizeStudentAction
    data class OnFaceDetected(val yaw: Float) : RecognizeStudentAction
    data object OnLivenessSuccess : RecognizeStudentAction
    data class OnRecognitionSuccess(val original: FloatArray, val mirrored: FloatArray) : RecognizeStudentAction
    data class OnFailure(val message: UiText) : RecognizeStudentAction
    data class OnPermissionResult(val isGranted: Boolean) : RecognizeStudentAction
    data object OnEmbeddingCaptureConsumed : RecognizeStudentAction
    data object OnCheatingDetected : RecognizeStudentAction // New: triggered when face disappears from camera during critical liveness steps
    data object OnDismissDialog : RecognizeStudentAction
    data object OnRetryFromDialog : RecognizeStudentAction
}
