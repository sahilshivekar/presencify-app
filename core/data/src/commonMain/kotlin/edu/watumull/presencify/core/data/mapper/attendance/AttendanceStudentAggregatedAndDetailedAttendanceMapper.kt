package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.AttendanceStudentAggregatedAndDetailedAttendanceDto
import edu.watumull.presencify.core.domain.model.attendance.AttendanceStudentAggregatedAndDetailed

fun AttendanceStudentAggregatedAndDetailedAttendanceDto.toDomain(): AttendanceStudentAggregatedAndDetailed = AttendanceStudentAggregatedAndDetailed(
    aggregatedAttendance = aggregatedAttendance.toDomain(),
    detailedAttendanceRecord = detailedAttendanceRecord.map { it.toDomain() }
)
