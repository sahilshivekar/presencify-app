package edu.watumull.presencify.core.data.mapper.shedule

import edu.watumull.presencify.core.data.dto.shedule.TimetableListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.shedule.TimetableListWithTotalCount

fun TimetableListWithTotalCountDto.toDomain(): TimetableListWithTotalCount = TimetableListWithTotalCount(
    timetables = timetables.map { it.toDomain() },
    totalCount = totalCount
)
