package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceSummaryDto(
    val courseId: String,
    val attendanceSummary: List<AttendanceRecordDto>
)
