package edu.watumull.presencify.domain

data class AttendanceRecord(
    val attendanceDate: String,
    val totalStudents: Int,
    val presentStudents: Int,
    val attendanceId: Int
)
