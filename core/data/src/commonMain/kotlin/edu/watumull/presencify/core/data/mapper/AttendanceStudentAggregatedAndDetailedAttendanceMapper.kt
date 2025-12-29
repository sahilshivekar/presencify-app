package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.AttendanceStudentAggregatedAndDetailedAttendanceDto
import edu.watumull.presencify.domain.AttendanceStudentAggregatedAndDetailed

fun AttendanceStudentAggregatedAndDetailedAttendanceDto.toDomain(): AttendanceStudentAggregatedAndDetailed = AttendanceStudentAggregatedAndDetailed(
    aggregatedAttendance = aggregatedAttendance.toDomain(),
    detailedAttendanceRecord = detailedAttendanceRecord.map { it.toDomain() }
)
