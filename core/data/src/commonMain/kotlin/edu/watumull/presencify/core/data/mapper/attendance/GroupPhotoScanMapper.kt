package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.GroupPhotoScanResponseDto
import edu.watumull.presencify.core.domain.model.attendance.GroupPhotoScanResult

fun GroupPhotoScanResponseDto.toDomain(): GroupPhotoScanResult =
    GroupPhotoScanResult(
        presentCount = presentCount,
        unknownFacesCount = unknownFacesCount,
        totalFacesDetected = totalFacesDetected,
        presentStudentIds = presentStudentIds,
    )
