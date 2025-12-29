package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.AttendanceStudentDto
import edu.watumull.presencify.core.domain.model.attendance.AttendanceStudent

fun AttendanceStudentDto.toDomain(): AttendanceStudent = AttendanceStudent(
    id = id,
    attendanceId = attendanceId,
    studentId = studentId,
    status = status,
    markedAt = markedAt,
    attendance = attendance?.toDomain(), // Avoid recursive mapping
    student = student?.toDomain()
)
