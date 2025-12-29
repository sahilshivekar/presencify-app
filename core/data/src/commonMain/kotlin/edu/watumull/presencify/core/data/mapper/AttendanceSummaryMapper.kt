package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.AttendanceSummaryDto
import edu.watumull.presencify.domain.AttendanceSummary

fun AttendanceSummaryDto.toDomain(): AttendanceSummary = AttendanceSummary(
    courseId = courseId,
    attendanceSummary = attendanceSummary.map { it.toDomain() }
)
