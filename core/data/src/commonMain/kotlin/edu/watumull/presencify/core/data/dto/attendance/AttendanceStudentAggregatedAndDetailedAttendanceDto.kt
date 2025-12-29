package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceStudentAggregatedAndDetailedAttendanceDto(
    val aggregatedAttendance: AggregatedAttendanceDto,
    val detailedAttendanceRecord: List<DetailedAttendanceRecordDto>
)
