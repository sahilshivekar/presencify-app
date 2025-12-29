package edu.watumull.presencify.core.domain.model.attendance

data class AttendanceAllStudents(
    val attendanceSummary: AttendanceSummary,
    val attendanceRecord: List<AttendanceRecord>
)
