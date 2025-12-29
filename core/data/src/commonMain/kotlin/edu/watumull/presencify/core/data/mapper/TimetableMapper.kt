package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.TimetableDto
import edu.watumull.presencify.core.domain.model.shedule.Timetable

fun TimetableDto.toDomain(): Timetable = Timetable(
    id = id,
    divisionId = divisionId,
    timetableVersion = timetableVersion,
    division = division?.toDomain(),
    classes = classes?.map { it.toDomain() }
)
