package edu.watumull.presencify.core.domain.model.attendance

data class AggregatedAttendance(
    val courseId: String,
    val courseName: String,
    val totalLectures: Int,
    val attendedLectures: Int
)
