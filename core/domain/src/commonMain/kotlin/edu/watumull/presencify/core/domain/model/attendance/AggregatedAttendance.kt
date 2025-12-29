package edu.watumull.presencify.domain

data class AggregatedAttendance(
    val courseId: String,
    val courseName: String,
    val totalLectures: Int,
    val attendedLectures: Int
)
