package edu.watumull.presencify.core.data.mapper.schedule

import edu.watumull.presencify.core.data.dto.schedule.UpcomingClassDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.data.mapper.teacher.toDomain
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.model.schedule.UpcomingClass
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun UpcomingClassDto.toDomain(): UpcomingClass {
    return UpcomingClass(
        id = id,
        nextClassDate = nextClassDate,
        startTime = startTime,
        endTime = endTime,
        dayOfWeek = dayOfWeek,
        course = course.toDomain(),
        room = room.toDomain(),
        teacher = teacher.toDomain()
    )
}