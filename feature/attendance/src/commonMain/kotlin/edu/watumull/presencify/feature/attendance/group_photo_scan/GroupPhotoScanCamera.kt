package edu.watumull.presencify.feature.attendance.group_photo_scan

import androidx.compose.runtime.Composable

/**
 * Platform-specific camera UI for capturing a single classroom photo.
 * On Android/iOS this should show a live camera preview and call [onImageCaptured]
 * with the captured image bytes. On desktop/JVM this can be a no-op or simple
 * placeholder that never calls [onImageCaptured].
 */
@Composable
expect fun GroupPhotoScanCamera(
    onImageCaptured: (ByteArray) -> Unit,
)
