package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.ClassDto
import edu.watumull.presencify.core.domain.model.shedule.ClassSession

fun ClassDto.toDomain(): ClassSession = ClassSession(
    id = id,
    timetableId = timetableId,
    batchId = batchId,
    roomId = roomId,
    courseId = courseId,
    teacherId = teacherId,
    startTime = startTime,
    endTime = endTime,
    dayOfWeek = dayOfWeek,
    activeFrom = activeFrom,
    activeTill = activeTill,
    classType = classType,
    isExtraClass = isExtraClass,
    timetable = timetable?.toDomain(),
    batch = batch?.toDomain(),
    room = room?.toDomain(),
    course = course?.toDomain(),
    teacher = teacher?.toDomain(),
    attendances = attendances?.map { it.toDomain() }
)
