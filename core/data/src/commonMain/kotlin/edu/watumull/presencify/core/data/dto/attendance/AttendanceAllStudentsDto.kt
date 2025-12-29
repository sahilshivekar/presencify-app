package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceAllStudentsDto(
    val attendanceSummary: AttendanceSummaryDto,
    val attendanceRecord: List<AttendanceRecordDto>
)
