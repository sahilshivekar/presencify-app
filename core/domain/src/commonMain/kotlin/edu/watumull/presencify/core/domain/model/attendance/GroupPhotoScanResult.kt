package edu.watumull.presencify.core.domain.model.attendance

data class GroupPhotoScanResult(
    val presentCount: Int,
    val unknownFacesCount: Int,
    val totalFacesDetected: Int,
    val presentStudentIds: List<String>,
)
