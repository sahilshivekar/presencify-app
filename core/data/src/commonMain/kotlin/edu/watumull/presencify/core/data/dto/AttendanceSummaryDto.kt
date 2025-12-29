package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceSummaryDto(
    val courseId: String,
    val attendanceSummary: List<AttendanceRecordDto>
)
