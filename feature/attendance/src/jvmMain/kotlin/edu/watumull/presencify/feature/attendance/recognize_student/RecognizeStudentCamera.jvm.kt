package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

@Composable
actual fun RecognizeStudentCamera(
    modifier: Modifier,
    onFaceDetected: (Float) -> Unit,
    onEmbeddingExtracted: (FloatArray) -> Unit,
    isLivenessComplete: Boolean,
    cameraPermissionGranted: Boolean,
    onPermissionResult: (Boolean) -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Camera Not Implemented on Jvm")
        // TODO: Implement Vision Framework + AVFoundation integration
    }
}
