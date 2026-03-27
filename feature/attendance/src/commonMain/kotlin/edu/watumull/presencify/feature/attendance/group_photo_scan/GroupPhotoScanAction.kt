package edu.watumull.presencify.feature.attendance.group_photo_scan

sealed interface GroupPhotoScanAction {
    data object NavigateBack : GroupPhotoScanAction
    data class AddImages(val newImages: List<ByteArray>) : GroupPhotoScanAction
    data class RemoveImage(val index: Int) : GroupPhotoScanAction
    data object SubmitGroupPhotoScan : GroupPhotoScanAction
    data object DismissDialog : GroupPhotoScanAction
    data object OpenCamera : GroupPhotoScanAction
    data object CloseCamera : GroupPhotoScanAction
}
