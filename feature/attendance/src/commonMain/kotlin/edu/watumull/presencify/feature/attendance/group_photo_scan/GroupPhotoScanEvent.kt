package edu.watumull.presencify.feature.attendance.group_photo_scan

sealed interface GroupPhotoScanEvent {
    data object NavigateBack : GroupPhotoScanEvent
}
