package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun RecognizeStudentCamera(
    modifier: Modifier,
    onFaceDetected: (Float) -> Unit,
    onEmbeddingExtracted: (FloatArray, FloatArray) -> Unit,
    isLivenessComplete: Boolean,
    shouldCaptureEmbedding: Boolean,
    cameraPermissionGranted: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onCheatingDetected: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Camera Not Implemented on Jvm")
    }
}
