package edu.watumull.presencify.domain

data class AttendanceAllStudents(
    val attendanceSummary: AttendanceSummary,
    val attendanceRecord: List<AttendanceRecord>
)
