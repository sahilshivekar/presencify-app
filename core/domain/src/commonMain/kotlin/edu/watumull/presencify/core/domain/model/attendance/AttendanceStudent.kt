package edu.watumull.presencify.core.domain.model.attendance

import edu.watumull.presencify.core.domain.model.student.Student

data class AttendanceStudent(
    val id: String,
    val attendanceId: String,
    val studentId: String,
    val status: String,
    val markedAt: String?,
    val attendance: Attendance? = null,
    val student: Student? = null
)