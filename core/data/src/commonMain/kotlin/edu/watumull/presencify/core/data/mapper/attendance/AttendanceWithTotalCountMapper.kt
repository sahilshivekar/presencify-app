package edu.watumull.presencify.core.data.mapper.attendance

import edu.watumull.presencify.core.data.dto.attendance.AttendanceWithTotalCountDto
import edu.watumull.presencify.core.domain.model.attendance.AttendanceWithTotalCount

fun AttendanceWithTotalCountDto.toDomain(): AttendanceWithTotalCount = AttendanceWithTotalCount(
    attendances = attendances.map { it.toDomain() },
    totalCount = totalCount
)
