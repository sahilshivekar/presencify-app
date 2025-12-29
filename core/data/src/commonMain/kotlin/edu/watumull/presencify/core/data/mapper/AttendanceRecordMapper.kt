package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.AttendanceRecordDto
import edu.watumull.presencify.domain.AttendanceRecord

fun AttendanceRecordDto.toDomain(): AttendanceRecord = AttendanceRecord(
    attendanceDate = attendanceDate,
    totalStudents = totalStudents,
    presentStudents = presentStudents,
    attendanceId = attendanceId
)
