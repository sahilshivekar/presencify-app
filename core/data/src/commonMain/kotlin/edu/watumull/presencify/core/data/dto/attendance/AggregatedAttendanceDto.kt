package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class AggregatedAttendanceDto(
    val courseId: String,
    val courseName: String,
    val totalLectures: Int,
    val attendedLectures: Int
)
