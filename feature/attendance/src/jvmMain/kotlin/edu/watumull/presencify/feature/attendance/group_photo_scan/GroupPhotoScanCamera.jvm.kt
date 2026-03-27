package edu.watumull.presencify.feature.attendance.group_photo_scan

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
actual fun GroupPhotoScanCamera(
    onImageCaptured: (ByteArray) -> Unit,
) {
    // Desktop/JVM placeholder – camera not supported here.
    Text("Camera capture is not available on this platform")
}
