package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.StudentAttendanceAggregatedAndDetailedDto
import edu.watumull.presencify.core.domain.model.attendance.AttendanceStudentAggregatedAndDetailed

fun StudentAttendanceAggregatedAndDetailedDto.toDomain(): AttendanceStudentAggregatedAndDetailed =
    AttendanceStudentAggregatedAndDetailed(
        aggregatedAttendance = aggregatedAttendance.map { it.toDomain() },
        detailedAttendanceRecord = detailedAttendance.map { it.toDomain() }
    )
