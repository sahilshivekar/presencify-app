package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.AggregatedAttendanceDto
import edu.watumull.presencify.domain.AggregatedAttendance

fun AggregatedAttendanceDto.toDomain(): AggregatedAttendance = AggregatedAttendance(
    courseId = courseId,
    courseName = courseName,
    totalLectures = totalLectures,
    attendedLectures = attendedLectures
)
