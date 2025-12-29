package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DetailedAttendanceRecordDto(
    val attendanceId: Int,
    val date: String,
    val attendanceStatus: Boolean
)
