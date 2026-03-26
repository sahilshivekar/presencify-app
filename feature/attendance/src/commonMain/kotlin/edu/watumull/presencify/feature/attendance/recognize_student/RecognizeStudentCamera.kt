package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun RecognizeStudentCamera(
    modifier: Modifier = Modifier,
    onFaceDetected: (Float) -> Unit,
    onEmbeddingExtracted: (FloatArray, FloatArray) -> Unit,
    isLivenessComplete: Boolean,
    shouldCaptureEmbedding: Boolean,
    cameraPermissionGranted: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onCheatingDetected: () -> Unit
)
