package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.AttendanceSummaryDto
import edu.watumull.presencify.core.domain.model.attendance.AttendanceSummary

fun AttendanceSummaryDto.toDomain(): AttendanceSummary = AttendanceSummary(
    courseId = courseId,
    attendanceSummary = attendanceSummary.map { it.toDomain() }
)
