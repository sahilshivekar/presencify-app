package edu.watumull.presencify.core.domain.model.attendance

data class AttendanceRecord(
    val attendanceDate: String,
    val totalStudents: Int,
    val presentStudents: Int,
    val attendanceId: Int
)
