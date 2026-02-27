package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceWithTotalCountDto(
    val attendances: List<AttendanceDto>,
    val totalCount: Int
)
