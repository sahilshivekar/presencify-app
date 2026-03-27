package edu.watumull.presencify.core.domain.model.attendance

data class GroupPhotoScanResult(
    val presentCount: Int,
    val presentStudentIds: List<String>,
)
