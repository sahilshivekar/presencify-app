package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class GroupPhotoScanResponseDto(
    val presentCount: Int,
    val unknownFacesCount: Int,
    val totalFacesDetected: Int,
    val presentStudentIds: List<String>,
)
