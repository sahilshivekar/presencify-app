package edu.watumull.presencify.feature.attendance.group_photo_scan

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText

data class GroupPhotoScanState(
    val attendanceId: String = "",
    val images: List<ByteArray> = emptyList(),
    val isLoading: Boolean = false,
    val dialogState: DialogState? = null,
    val isCameraOpen: Boolean = false,
) {
    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val title: String = "",
        val message: UiText? = null,
    )
}
