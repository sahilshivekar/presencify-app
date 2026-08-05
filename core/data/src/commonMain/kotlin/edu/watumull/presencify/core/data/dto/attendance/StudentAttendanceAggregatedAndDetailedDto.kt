package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class StudentAttendanceAggregatedAndDetailedDto(
    val studentId: String,
    val aggregatedAttendance: List<AggregatedAttendanceDto>,
    val detailedAttendance: List<DetailedAttendanceRecordDto>
)
