package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceStudentAggregatedAndDetailedAttendanceDto(
    val aggregatedAttendance: AggregatedAttendanceDto,
    val detailedAttendanceRecord: List<DetailedAttendanceRecordDto>
)
