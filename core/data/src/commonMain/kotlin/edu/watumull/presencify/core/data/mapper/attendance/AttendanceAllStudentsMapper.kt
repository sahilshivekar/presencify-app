package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.AttendanceAllStudentsDto
import edu.watumull.presencify.core.domain.model.attendance.AttendanceAllStudents

fun AttendanceAllStudentsDto.toDomain(): AttendanceAllStudents = AttendanceAllStudents(
    attendanceSummary = attendanceSummary.toDomain(),
    attendanceRecord = attendanceRecord.map { it.toDomain() }
)
