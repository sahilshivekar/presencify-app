package edu.watumull.presencify.core.domain.model.attendance

data class AttendanceSummary(
    val courseId: String,
    val attendanceSummary: List<AttendanceRecord>
)
