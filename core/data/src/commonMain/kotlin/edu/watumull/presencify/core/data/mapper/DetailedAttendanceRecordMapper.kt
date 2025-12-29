package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.DetailedAttendanceRecordDto
import edu.watumull.presencify.domain.DetailedAttendanceRecord

fun DetailedAttendanceRecordDto.toDomain(): DetailedAttendanceRecord = DetailedAttendanceRecord(
    attendanceId = attendanceId,
    date = date,
    attendanceStatus = attendanceStatus
)
