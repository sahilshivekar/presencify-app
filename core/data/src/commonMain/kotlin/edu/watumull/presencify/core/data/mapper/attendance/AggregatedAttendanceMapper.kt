package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.AggregatedAttendanceDto
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance


fun AggregatedAttendanceDto.toDomain(): AggregatedAttendance = AggregatedAttendance(
    courseId = courseId,
    courseName = courseName,
    totalLectures = totalLectures,
    attendedLectures = attendedLectures
)
