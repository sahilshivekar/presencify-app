package edu.watumull.presencify.domain

data class AttendanceSummary(
    val courseId: String,
    val attendanceSummary: List<AttendanceRecord>
)
