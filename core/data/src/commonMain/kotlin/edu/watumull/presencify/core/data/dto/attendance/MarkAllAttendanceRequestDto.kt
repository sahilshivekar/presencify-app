package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class MarkAllAttendanceRequestDto(
    val attendanceId: String
)