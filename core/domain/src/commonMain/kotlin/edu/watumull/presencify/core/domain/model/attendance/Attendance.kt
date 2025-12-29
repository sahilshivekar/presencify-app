package edu.watumull.presencify.core.domain.model.attendance

import edu.watumull.presencify.core.domain.model.shedule.ClassSession

data class Attendance(
    val id: String,
    val classId: String,
    val date: String,
    val totalStudents: Int,
    val presentCount: Int,
    val absentCount: Int,
    val klass: ClassSession? = null,
    val attendanceStudents: List<AttendanceStudent>? = null
)