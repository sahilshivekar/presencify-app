package edu.watumull.presencify.core.data.mapper.shedule

import edu.watumull.presencify.core.data.dto.shedule.TimetableDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.domain.model.shedule.Timetable

fun TimetableDto.toDomain(): Timetable = Timetable(
    id = id,
    divisionId = divisionId,
    timetableVersion = timetableVersion,
    division = division?.toDomain(),
    classes = classes?.map { it.toDomain() }
)
