package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.TimetableListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.shedule.TimetableListWithTotalCount

fun TimetableListWithTotalCountDto.toDomain(): TimetableListWithTotalCount = TimetableListWithTotalCount(
    timetables = timetables.map { it.toDomain() },
    totalCount = totalCount
)
