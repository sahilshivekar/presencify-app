package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.DetailedAttendanceRecordDto
import edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord

fun DetailedAttendanceRecordDto.toDomain(): DetailedAttendanceRecord = DetailedAttendanceRecord(
    attendanceId = attendanceId,
    date = date,
    attendanceStatus = attendanceStatus
)
