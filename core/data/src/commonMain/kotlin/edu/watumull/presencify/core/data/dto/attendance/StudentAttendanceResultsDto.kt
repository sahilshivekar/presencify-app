package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class StudentAttendanceResultsDto(
    val attendanceByStudent: List<StudentAttendanceAggregatedAndDetailedDto>
)
