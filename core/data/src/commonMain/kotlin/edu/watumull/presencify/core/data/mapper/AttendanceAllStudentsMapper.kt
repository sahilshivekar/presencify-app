package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.AttendanceAllStudentsDto
import edu.watumull.presencify.domain.AttendanceAllStudents

fun AttendanceAllStudentsDto.toDomain(): AttendanceAllStudents = AttendanceAllStudents(
    attendanceSummary = attendanceSummary.toDomain(),
    attendanceRecord = attendanceRecord.map { it.toDomain() }
)
