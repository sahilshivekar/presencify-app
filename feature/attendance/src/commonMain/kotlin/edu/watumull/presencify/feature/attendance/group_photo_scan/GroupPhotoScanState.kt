package edu.watumull.presencify.feature.attendance.group_photo_scan

import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class GroupPhotoScanState(
    val attendanceId: String = "",
    val images: List<ByteArray> = emptyList(),
    val isLoading: Boolean = false,
    val dialogState: DialogState? = null,
    val isCameraOpen: Boolean = false,
)
