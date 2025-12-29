package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.AttendanceRecordDto
import edu.watumull.presencify.core.domain.model.attendance.AttendanceRecord

fun AttendanceRecordDto.toDomain(): AttendanceRecord = AttendanceRecord(
    attendanceDate = attendanceDate,
    totalStudents = totalStudents,
    presentStudents = presentStudents,
    attendanceId = attendanceId
)
